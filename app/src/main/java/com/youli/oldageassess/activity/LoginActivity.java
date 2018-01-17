package com.youli.oldageassess.activity;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.youli.oldageassess.R;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.ProgressDialogUtils;
import com.youli.oldageassess.utils.SharedPreferencesUtils;

import java.io.IOException;

import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext=this;

    private Button btnLogin;//登录按钮

    private EditText etUserName,etPwd;

    private String userNameStr,pwdStr;

    private final int SUCCEED=10000;
    private final int  PROBLEM=10001;

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            ProgressDialogUtils.dismissMyProgressDialog(LoginActivity.this);
            switch (msg.what){

                case SUCCEED:

                    if(TextUtils.equals("true", (String)msg.obj)){
                        Intent intent=new Intent(LoginActivity.this,NeedAssessActivity.class);
                        startActivity(intent);
                        SharedPreferencesUtils.putString("userName",userNameStr);
                        finish();
                    }else if(TextUtils.equals("false", (String)msg.obj)){
                        Toast.makeText(LoginActivity.this,"用户名或密码不正确",Toast.LENGTH_SHORT).show();
                    }

                    break;

                case PROBLEM:

                    Toast.makeText(LoginActivity.this,"网络不给力",Toast.LENGTH_SHORT).show();

                    break;
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews(){

        btnLogin=findViewById(R.id.btn_dl);
        btnLogin.setOnClickListener(this);

        etUserName=findViewById(R.id.et_username);
        etPwd=findViewById(R.id.et_pwd);

        String localUserName = SharedPreferencesUtils.getString("userName");
        if (!TextUtils.equals("",localUserName)) {
            etUserName.setText(localUserName);
            etPwd.requestFocus();
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_dl:

                userNameStr=etUserName.getText().toString().trim();
               pwdStr=etPwd.getText().toString().trim();

                if(TextUtils.equals("",userNameStr)||TextUtils.equals("",pwdStr)){
                    Toast.makeText(this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                }else{

                        //登录
                        login(userNameStr,pwdStr);

                }

                break;

        }

    }

    private void login(final String name, final String password){

        ProgressDialogUtils.showMyProgressDialog(this);


        new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        String url= MyOkHttpUtils.BaseUrl+"/login.aspx?username="+name+"&password="+password;

                        Log.e("2017/11/9","登录="+url);

                        Response response=MyOkHttpUtils.okHttpGet(url);

                        //获得cookies
                        if(response!=null) {
                            if (response.header("Set-Cookie") != null) {
                                String cookies = response.header("Set-Cookie").toString();

                                String mycookies=cookies.substring(0,cookies.indexOf(";"));
                                SharedPreferencesUtils.putString("cookie", mycookies);

                            }
                        }
                        Message msg=Message.obtain();
                        try {
                            if(response!=null) {
                                msg.obj = response.body().string();
                                msg.what=SUCCEED;
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

}
