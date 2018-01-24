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

    private final int SUCCEED = 10000;//信息获取成功
    private final int PROBLEM = 10001;//信息获取失败
    private final int OVERTIME=10005;//登录超时

    private ProgressDialog pd;
    private Context mContext=this;
    private ImageView ivWdc,ivYdc;

    private AdminInfo adminInfo;//操作员信息

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
          dismissMyProgressDialog(mContext);
            switch (msg.what) {

                case SUCCEED://信息获取成功

                    adminInfo=(AdminInfo)(msg.obj);
                    ivWdc.setEnabled(true);
                    ivYdc.setEnabled(true);

                    break;


                case PROBLEM://信息获取失败
                    ivWdc.setEnabled(false);
                    ivYdc.setEnabled(false);

                    break;

            }
        }
    };


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
        ivWdc.setEnabled(false);
        ivYdc.setEnabled(false);

        getAdminInfo();//获取操作员ID

    }

    private void getAdminInfo(){

        showMyProgressDialog(mContext);

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

                                            msg.what=SUCCEED;
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


    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){

            case R.id.iv_wdc_need_assess://未调查


                 i=new Intent(mContext,PersonListActivity.class);
                 i.putExtra("type",1);
                 i.putExtra("adminInfo",adminInfo);
                 startActivity(i);

                break;

            case R.id.iv_ydc_need_assess://已调查

                i=new Intent(mContext,PersonListActivity.class);
                i.putExtra("type",2);
                i.putExtra("adminInfo",adminInfo);
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

    private void showMyProgressDialog(Context context){

        pd=new ProgressDialog(context);
        pd.setTitle("正在加载中...");
        pd.setCancelable(false);
        pd.show();
    }

    private void dismissMyProgressDialog(Context context){

        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
            pd=null;
        }

    }

}
