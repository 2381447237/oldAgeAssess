package com.youli.oldageassess.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.youli.oldageassess.R;


/**
 * Created by liutao on 2018/1/11.
 *
 * 需求评估
 */

public class NeedAssessActivity extends BaseActivity implements View.OnClickListener{


    private Context mContext=this;
    private ImageView ivWdc,ivYdc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_assess);

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
                 startActivity(i);

                break;

            case R.id.iv_ydc_need_assess://已调查

                i=new Intent(mContext,PersonListActivity.class);
                i.putExtra("type",2);
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
