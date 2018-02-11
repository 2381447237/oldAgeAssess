package com.youli.oldageassess.activity;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youli.oldageassess.R;
import com.youli.oldageassess.entity.AdminInfo;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.ProgressDialogUtils;
import com.youli.oldageassess.utils.SharedPreferencesUtils;
import com.youli.oldageassess.utils.TextViewUtils;
import com.youli.oldageassess.utils.UpdateManager;

import java.io.IOException;

import okhttp3.Response;



public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext=this;

    private Button btnLogin;//登录按钮

    private EditText etUserName,etPwd,etUserName2,etPwd2;

    private String userNameStr,pwdStr,userNameStr2,pwdStr2;

    private TextView nameTv,nameTv2;//评估员姓名1，评估员姓名2

    private final int SUCCEED_LOGIN=10000;//登录1按钮
    private final int SUCCEED_LOGIN2=10001;//登录2按钮
    private final int SUCCEED_ADMININFO = 10002;//调查员1信息获取成功
    private final int SUCCEED_ADMININFO2 = 10003;//调查员2信息获取成功
    private final int  PROBLEM=10004;
    private final int SUCCEED_NAME=10005;//评估员的姓名
    private final int  PROBLEM_NAME=10006;
    private final int OVERTIME=10007;//登录超时

    private AdminInfo adminInfo,adminInfo2;//操作员信息

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            ProgressDialogUtils.dismissMyProgressDialog(LoginActivity.this);
            switch (msg.what){

                case SUCCEED_LOGIN:

                                       if(TextUtils.equals("true", (String)msg.obj)){
                                           getAdminInfo("one");//获取操作员ID
                    }else if(TextUtils.equals("false", (String)msg.obj)){
                        Toast.makeText(LoginActivity.this,"用户名或密码不正确",Toast.LENGTH_SHORT).show();
                    }

                    break;

                case SUCCEED_LOGIN2:

                    if(TextUtils.equals("true", (String)msg.obj)){
                        getAdminInfo("two");//获取操作员2ID
                    }else if(TextUtils.equals("false", (String)msg.obj)){
                        Toast.makeText(LoginActivity.this,"用户名或密码不正确",Toast.LENGTH_SHORT).show();
                    }

                    break;

                case SUCCEED_ADMININFO:

                    adminInfo=(AdminInfo)(msg.obj);
                    //登录
                    login(etUserName2.getText().toString().trim(),etPwd2.getText().toString().trim(),"two");

                    break;

                case SUCCEED_ADMININFO2:
                    adminInfo2=(AdminInfo)(msg.obj);

                    Log.e("2018-1-25","adminInfo111=="+adminInfo.getNAME());
                    Log.e("2018-1-25","adminInfo222=="+adminInfo2.getNAME());
                                            Intent intent=new Intent(LoginActivity.this,NeedAssessActivity.class);
                        intent.putExtra("adminInfo",adminInfo);
                        intent.putExtra("adminInfo2",adminInfo2);
                        startActivity(intent);
                        SharedPreferencesUtils.putString("userName",userNameStr);
                        SharedPreferencesUtils.putString("userName2",userNameStr2);
                        finish();

                    break;

                case PROBLEM:

                    if(msg.arg1==PROBLEM_NAME){
                        Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(LoginActivity.this, "网络不给力", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case SUCCEED_NAME:

                    if(TextUtils.equals(msg.obj+"","False")){
                        if(msg.arg1==1) {
                            nameTv.setText(TextViewUtils.appendOneSpace("评估员A"));
                        }else if(msg.arg1==2){
                            nameTv2.setText(TextViewUtils.appendOneSpace("评估员B"));
                        }
                        Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();
                    }else {
                        if(msg.arg1==1) {
                            nameTv.setText(TextViewUtils.appendOneSpace(msg.obj + ""));
                        }else if(msg.arg1==2){
                            nameTv2.setText(TextViewUtils.appendOneSpace(msg.obj + ""));
                        }
                    }
                    break;
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 更新apk
        UpdateManager manager = new UpdateManager(LoginActivity.this);
        manager.checkUpdate();

        initViews();
    }

    private void initViews(){

        nameTv=findViewById(R.id.login_name1);
        nameTv.setText(TextViewUtils.appendOneSpace(nameTv.getText().toString()));
        nameTv2=findViewById(R.id.login_name2);
        nameTv2.setText(TextViewUtils.appendOneSpace(nameTv2.getText().toString()));
        btnLogin=findViewById(R.id.btn_dl);
        btnLogin.setOnClickListener(this);

        etUserName=findViewById(R.id.et_username);
        etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          @Override
          public void onFocusChange(View view, boolean b) {

              if(!b&&etUserName.getText().toString().trim().length()>0){//失去焦点
                  getLoginName(etUserName,1);
              }

          }
      });
        etUserName2=findViewById(R.id.et_username2);
        etUserName2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(!b&&etUserName2.getText().toString().trim().length()>0){//失去焦点
                    getLoginName(etUserName2,2);
                }

            }
        });
        etPwd=findViewById(R.id.et_pwd);
        etPwd2=findViewById(R.id.et_pwd2);
        String localUserName = SharedPreferencesUtils.getString("userName");
        if (!TextUtils.equals("",localUserName)) {
            etUserName.setText(localUserName);
            etPwd.requestFocus();
        }
        String localUserName2 = SharedPreferencesUtils.getString("userName2");
        if (!TextUtils.equals("",localUserName2)) {
            etUserName2.setText(localUserName2);

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_dl:

                userNameStr=etUserName.getText().toString().trim();
                userNameStr2=etUserName2.getText().toString().trim();
               pwdStr=etPwd.getText().toString().trim();
                pwdStr2=etPwd2.getText().toString().trim();

                if(TextUtils.equals(userNameStr,userNameStr2)){
                    Toast.makeText(this,"用户名重复",Toast.LENGTH_SHORT).show();

                    return;
                }

                if(TextUtils.equals("",userNameStr)||TextUtils.equals("",pwdStr)||TextUtils.equals("",userNameStr2)||TextUtils.equals("",pwdStr2)){
                    Toast.makeText(this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                }else{

                        //登录
                        login(userNameStr,pwdStr,"one");

                }

                break;

        }

    }

    private void login(final String name, final String password, final String mark){

        ProgressDialogUtils.showMyProgressDialog(this);

        new Thread(

                new Runnable() {
                    @Override
                    public void run() {

                          String  url= MyOkHttpUtils.BaseUrl + "/login.aspx?username=" + name + "&password=" + password;

                        Log.e("2017/11/9","登录="+url);

                        Response response=MyOkHttpUtils.okHttpGet(url);

                        //获得cookies
                        if(response!=null) {
                            if (response.header("Set-Cookie") != null) {
                                String cookies = response.header("Set-Cookie").toString();

                                String mycookies=cookies.substring(0,cookies.indexOf(";"));
                                SharedPreferencesUtils.putString("cookies", mycookies);

                            }
                        }
                        Message msg=Message.obtain();
                        try {
                            if(response!=null) {
                                msg.obj = response.body().string();
                                if(TextUtils.equals(mark,"one")) {
                                    msg.what=SUCCEED_LOGIN;
                                }else if(TextUtils.equals(mark,"two")) {
                                    msg.what=SUCCEED_LOGIN2;
                                }

                                mHandler.sendMessage(msg);
                            }else{
                                msg.what=PROBLEM;
                                mHandler.sendMessage(msg);

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

        ).start();

    }

    private void getLoginName(EditText et, final int type){//获取评估员的姓名

        final String etStr=et.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {

                new Thread(

                        new Runnable() {
                            @Override
                            public void run() {
                                //http://183.194.4.58:81/Json/GetStaffName.aspx?input_code=admin
                                String url= MyOkHttpUtils.BaseUrl+"/Json/GetStaffName.aspx?input_code="+etStr;

                                Log.e("2018-1-24","url=="+url);

                                Response response=MyOkHttpUtils.okHttpGet(url);

                                Message msg=Message.obtain();

                                if(response!=null){

                                    if(response.body()!=null){

                                        try {
                                            String nameStr=response.body().string();

                                            if(!TextUtils.equals(nameStr,"")){


                                                    msg.obj=nameStr;
                                                    msg.arg1=type;
                                                    msg.what=SUCCEED_NAME;

                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            msg.arg1=PROBLEM_NAME;
                                            msg.what=PROBLEM;
                                        }

                                    }else{
                                        msg.arg1=PROBLEM_NAME;
                                        msg.what=PROBLEM;
                                    }

                                }else{
                                    msg.arg1=PROBLEM_NAME;
                                    msg.what=PROBLEM;

                                }

                                mHandler.sendMessage(msg);

                            }
                        }

                ).start();


            }
        }).start();

    }

    private void getAdminInfo(final String mark){


        //      showMyProgressDialog(mContext);

        new Thread(

                new Runnable() {
                    @Override
                    public void run() {

                        String url= MyOkHttpUtils.BaseUrl+"/Json/Get_Staff.aspx";

                        Response response=MyOkHttpUtils.okHttpGet(url);

                        Message msg=Message.obtain();

                        if(response!=null){

                            if(response.body()!=null){

                                try {
                                    String resStr=response.body().string();

                                    if(!TextUtils.equals(resStr,"")){

                                        Gson gson=new Gson();

                                        try{
                                            msg.obj=gson.fromJson(resStr,AdminInfo.class);


                                            if(TextUtils.equals("one",mark)) {
                                                msg.what = SUCCEED_ADMININFO;
                                            }else  if(TextUtils.equals("two",mark)) {
                                                msg.what = SUCCEED_ADMININFO2;
                                            }
                                        }catch(Exception e){
                                            msg.what=OVERTIME;

                                        }



                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }else{
                                msg.what=PROBLEM;
                            }

                        }else{

                            msg.what=PROBLEM;

                        }

                        mHandler.sendMessage(msg);

                    }
                }

        ).start();

    }


}
