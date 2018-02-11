package com.youli.oldageassess.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youli.oldageassess.R;
import com.youli.oldageassess.entity.AdminInfo;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.ProgressDialogUtils;
import com.youli.oldageassess.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import okhttp3.Response;


/**
 * Created by liutao on 2018/1/11.
 *
 * 需求评估
 */

public class NeedAssessActivity extends BaseActivity implements View.OnClickListener{



    private ProgressDialog pd;
    private Context mContext=this;
    private ImageView ivWdc,ivYdc;

    private AdminInfo adminInfo,adminInfo2;//操作员信息



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_assess);

        adminInfo=(AdminInfo)getIntent().getSerializableExtra("adminInfo");
        adminInfo2=(AdminInfo)getIntent().getSerializableExtra("adminInfo2");
        initViews();
    }

    private void initViews(){

        ivWdc=findViewById(R.id.iv_wdc_need_assess);
        ivYdc=findViewById(R.id.iv_ydc_need_assess);
        ivWdc.setOnClickListener(this);
        ivYdc.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){

            case R.id.iv_wdc_need_assess://未调查


                 i=new Intent(mContext,PersonListActivity.class);
                 i.putExtra("type",1);
                 i.putExtra("adminInfo",adminInfo);
                 i.putExtra("adminInfo2",adminInfo2);
                 startActivity(i);

                break;

            case R.id.iv_ydc_need_assess://已调查

                i=new Intent(mContext,PersonListActivity.class);
                i.putExtra("type",2);
                i.putExtra("adminInfo",adminInfo);
                i.putExtra("adminInfo2",adminInfo2);
                startActivity(i);
                break;
        }

    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("您确定退出吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ActivityController.finishAll();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }



}
