package com.youli.oldageassess.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.youli.oldageassess.R;
import com.youli.oldageassess.adapter.InvestPageFragmentAdapter;
import com.youli.oldageassess.entity.AdminInfo;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.fragment.CxsmFragment;
import com.youli.oldageassess.fragment.JbxmFragment;
import com.youli.oldageassess.fragment.JbzdFragment;
import com.youli.oldageassess.fragment.JtztFragment;
import com.youli.oldageassess.fragment.MyBaseFragment;
import com.youli.oldageassess.fragment.ZtzkFragment;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.ProgressDialogUtils;
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
import java.util.HashMap;
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

    public PersonInfo pInfo;
    public HashMap<String,Button> map=new HashMap<>();
    public RadioButton rbOne,rbTwo,rbThree,rbFour,rbFive;

    public List<InvestInfo> jtztList=new ArrayList<>();

    public int typeId;//他等于1时是未答，他等于2时是已答，
    public AdminInfo adminInfo;//操作员信息
    public ViewPager vp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);

        pInfo=(PersonInfo)getIntent().getSerializableExtra("pInfo");
        adminInfo=(AdminInfo) getIntent().getSerializableExtra("adminInfo");
        typeId=getIntent().getIntExtra("type",0);

        Log.e("2018-1-22","typeId=="+typeId);

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



        for(InvestInfo info:PersonListActivity.investInfo){
            //if(info.getTYPE_ID()==5){

                jtztList.add(info);

    // }

        }

        cxsmF = new CxsmFragment();
        jtztF= JtztFragment.newInstance(jtztList,pInfo);
        jbxmF = new JbxmFragment();
        ztzkF = new ZtzkFragment();
        jbzdF= new JbzdFragment();
        ArrayList<MyBaseFragment> page_list = new ArrayList<>();
        page_list.add(cxsmF);
        page_list.add(jtztF);
        page_list.add(jbxmF);
        page_list.add(ztzkF);
        page_list.add(jbzdF);

        vp = findViewById(R.id.mainfl);
        vp.setAdapter(new InvestPageFragmentAdapter(getSupportFragmentManager(),page_list));
        vp.setOffscreenPageLimit(5);

        vp.setCurrentItem(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ProgressDialogUtils.dismissMyProgressDialog(mContext);
            }
        },1500);
    }
//        fm.beginTransaction()
//                .add(R.id.mainfl, cxsmF)
//                .add(R.id.mainfl, jtztF)
//                .add(R.id.mainfl, jbxmF)
//                .add(R.id.mainfl, ztzkF)
//                .add(R.id.mainfl, jbzdF)
//                .commit();
//        fm.beginTransaction().hide(jbzdF).hide(ztzkF).hide(jbxmF).hide(jtztF).show(cxsmF).commit();
//    }

    //写着写着换思路了，发现这个方法没用（2018-1-21）
//    public void onChange(View v){
//
//        switch (v.getId()) {
//
//            case R.id.rb_one:
//                fm.beginTransaction().hide(jbzdF).hide(ztzkF).hide(jbxmF).hide(jtztF).show(cxsmF).commit();
//                break;
//            case R.id.rb_two:
//                cxsmF.btnStart.setVisibility(View.GONE);
//                fm.beginTransaction().hide(jbzdF).hide(ztzkF).hide(jbxmF).hide(cxsmF).show(jtztF).commit();
//                break;
//            case R.id.rb_three:
//                cxsmF.btnStart.setVisibility(View.GONE);
//                fm.beginTransaction().hide(jbzdF).hide(ztzkF).hide(cxsmF).hide(jtztF).show(jbxmF).commit();
//                break;
//            case R.id.rb_four:
//                cxsmF.btnStart.setVisibility(View.GONE);
//                fm.beginTransaction().hide(jbzdF).hide(cxsmF).hide(jbxmF).hide(jtztF).show(ztzkF).commit();
//                break;
//            case R.id.rb_five:
//                cxsmF.btnStart.setVisibility(View.GONE);
//                fm.beginTransaction().hide(cxsmF).hide(ztzkF).hide(jbxmF).hide(jtztF).show(jbzdF).commit();
//                break;
//        }

 //   }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  String info = data.getStringExtra("data");

        if (data != null) {
            String title = data.getStringExtra("Title");
            Log.e("2018-1-23", "String info = data.getStringExtra====" + title);
            if (map.get(title) != null) {
                map.get(title).setText(title + "(完成)");
            }
        }
    }
}
