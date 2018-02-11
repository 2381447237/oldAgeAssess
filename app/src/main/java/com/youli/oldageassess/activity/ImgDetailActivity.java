package com.youli.oldageassess.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.youli.oldageassess.R;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.utils.IOUtil;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.ProgressDialogUtils;

import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by liutao on 2018/1/29.
 *
 * 图片详情
 */

public class ImgDetailActivity extends BaseActivity{

    private Context mContext=this;

    private ProgressDialog progressDialog;

    private PersonInfo pInfo;

    private final int SUCCEED_PHOTO=10001;
    private final int  PROBLEM=10002;
    private final int OVERTIME=10003;//登录超时

    private ImageView ivDetail;

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
       dismissMyProgressDialog(mContext);
            switch (msg.what){

                case SUCCEED_PHOTO://照片

                   ivDetail.setImageBitmap((Bitmap) msg.obj);

                    break;

                case PROBLEM:

                    break;

                case OVERTIME://登录超时

                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_detail);

        pInfo=(PersonInfo) getIntent().getSerializableExtra("pInfo");

        ivDetail=findViewById(R.id.iv_img_detail);
        getPic();//照片
    }

    private void  getPic(){
       showMyProgressDialog(mContext);
        new Thread(

                new Runnable() {
                    @Override
                    public void run() {

                        //  http://183.194.4.58:81/Json/Get_Img.aspx?SQH=0013CH101201801020002&Img_No=1
                        String urlPic = MyOkHttpUtils.BaseUrl+"/Json/Get_Img.aspx?SQH="+pInfo.getSQH()+"&Img_No="+getIntent().getIntExtra("num",0);
                        Response response = MyOkHttpUtils.okHttpGet(urlPic);
                        try {
                            Message msg = Message.obtain();

                            if (response != null) {
                                InputStream is = response.body().byteStream();

                                byte[] picData = IOUtil.getBytesByStream(is);
                                //     picByte=picData;
                                Bitmap btp = BitmapFactory.decodeByteArray(picData, 0, picData.length);

                                msg.obj = btp;
                                msg.what = SUCCEED_PHOTO;
                                mHandler.sendMessage(msg);

                            } else {

                                // sendProblemMessage(msg);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

        ).start();

    }

    private void showMyProgressDialog(Context context){

        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("正在加载中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissMyProgressDialog(Context context){

        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog=null;
        }

    }

}
