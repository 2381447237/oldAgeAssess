package com.youli.oldageassess.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.youli.oldageassess.R;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.fragment.CxsmFragment;
import com.youli.oldageassess.fragment.JbxmFragment;
import com.youli.oldageassess.fragment.JbzdFragment;
import com.youli.oldageassess.fragment.JtztFragment;
import com.youli.oldageassess.fragment.ZtzkFragment;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.SharedPreferencesUtils;
import com.youli.oldageassess.utils.TextViewUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liutao on 2018/1/13.
 *
 * 问卷调查
 */

public class InvestActivity extends FragmentActivity {

    private Context mContext=this;

    private ImageView ivBack;

  //  private String [] title={"诚信声明","家庭状态","基本项目","总体状况","疾病诊断"};

    private FragmentManager fm=this.getSupportFragmentManager();

    public CxsmFragment cxsmF;
    public JtztFragment jtztF;
    public JbxmFragment jbxmF;
    public ZtzkFragment ztzkF;
    public JbzdFragment jbzdF;

    private TextView tvTitle;

    private PersonInfo pInfo;

    public RadioButton rbOne,rbTwo,rbThree,rbFour,rbFive;

    private List<InvestInfo> investInfo;
    private List<InvestInfo> jtztList=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);

        pInfo=(PersonInfo)getIntent().getSerializableExtra("pInfo");

        investInfo=(List<InvestInfo>)getIntent().getSerializableExtra("investInfo");

        rbOne=findViewById(R.id.rb_one);
        rbTwo=findViewById(R.id.rb_two);
        rbThree=findViewById(R.id.rb_three);
        rbFour=findViewById(R.id.rb_four);
        rbFive=findViewById(R.id.rb_five);

        tvTitle=findViewById(R.id.tv_title_invest);

        ivBack=findViewById(R.id.iv_back_invest);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });


        tvTitle.setText(TextViewUtils.appendSpace(pInfo.getXM()));

        for(InvestInfo info:investInfo){

            if(info.getTYPE_ID()==2){

                jtztList.add(info);

            }

        }

        cxsmF = new CxsmFragment();
        jtztF= JtztFragment.newInstance(jtztList,pInfo);
        jbxmF = new JbxmFragment();
        ztzkF = new ZtzkFragment();
        jbzdF= new JbzdFragment();
        fm.beginTransaction()
                .add(R.id.mainfl, cxsmF)
                .add(R.id.mainfl, jtztF)
                .add(R.id.mainfl, jbxmF)
                .add(R.id.mainfl, ztzkF)
                .add(R.id.mainfl, jbzdF)
                .commit();
        fm.beginTransaction().hide(jbzdF).hide(ztzkF).hide(jbxmF).hide(jtztF).show(cxsmF).commit();
    }

    public void onChange(View v){

        switch (v.getId()) {

            case R.id.rb_one:
                fm.beginTransaction().hide(jbzdF).hide(ztzkF).hide(jbxmF).hide(jtztF).show(cxsmF).commit();
                break;
            case R.id.rb_two:
                fm.beginTransaction().hide(jbzdF).hide(ztzkF).hide(jbxmF).hide(cxsmF).show(jtztF).commit();
                break;
            case R.id.rb_three:
                fm.beginTransaction().hide(jbzdF).hide(ztzkF).hide(cxsmF).hide(jtztF).show(jbxmF).commit();
                break;
            case R.id.rb_four:
                fm.beginTransaction().hide(jbzdF).hide(cxsmF).hide(jbxmF).hide(jtztF).show(ztzkF).commit();
                break;
            case R.id.rb_five:
                fm.beginTransaction().hide(cxsmF).hide(ztzkF).hide(jbxmF).hide(jtztF).show(jbzdF).commit();
                break;
        }

    }
    @Override
    public void onBackPressed() {
        showAlertDialog();
    }


    private void showAlertDialog(){

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("您确定退出答题吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }

//    //诚信声明fragment
//    public CxsmFragment getCxsmFragment(){
//
//        FragmentManager sfm=getSupportFragmentManager();
//        CxsmFragment cxsmFragment= (CxsmFragment) sfm.findFragmentByTag("cxsmF");
//
//        return  cxsmFragment;
//    }
//
//    //家庭状态fragment
//    public JtztFragment getJtztFragment(){
//
//        FragmentManager sfm=getSupportFragmentManager();
//        JtztFragment jtztFragment= (JtztFragment) sfm.findFragmentByTag("jtztF");
//
//        return  jtztFragment;
//    }
//
//    //基本项目fragment
//    public JbxmFragment getJbxmFragment(){
//
//        FragmentManager sfm=getSupportFragmentManager();
//        JbxmFragment jbxmFragment= (JbxmFragment) sfm.findFragmentByTag("jbxmF");
//
//        return  jbxmFragment;
//    }
//
//    //总体状况fragment
//    public ZtzkFragment getZtzkFragment(){
//
//        FragmentManager sfm=getSupportFragmentManager();
//        ZtzkFragment ztzkFragment= (ZtzkFragment) sfm.findFragmentByTag("ztzkF");
//
//        return  ztzkFragment;
//    }
//
//    //疾病诊断fragment
//    public JbzdFragment getJbzdFragment(){
//
//        FragmentManager sfm=getSupportFragmentManager();
//        JbzdFragment jbzdFragment= (JbzdFragment) sfm.findFragmentByTag("jbzdF");
//
//        return  jbzdFragment;
//    }

}
