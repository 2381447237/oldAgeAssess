package com.youli.oldageassess.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.youli.oldageassess.R;
import com.youli.oldageassess.activity.ImgDetailActivity;
import com.youli.oldageassess.activity.InvestActivity;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.PhotoUtils;
import com.youli.oldageassess.utils.SharedPreferencesUtils;
import com.youli.oldageassess.utils.ToastUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.List;

/**
 * Created by liutao on 2018/1/13.
 *
 * 诚信声明
 */

public class CxsmFragment extends MyBaseFragment implements View.OnClickListener{


    private final int SUCCEED_DELETE=10001;
    private final int  PROBLEM=10002;
    private final int OVERTIME=10003;//登录超时

    private ProgressDialog progressDialog;

    private  final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;

    private View view;

    public Button btnStart,btnPhoto;

    private FragmentManager fm;

    public ImageView ivPhoto1,ivPhoto2,ivPhoto3;//照片

    public ImageView ivDelete1,ivDelete2,ivDelete3;//删除

    private InvestActivity a;

    private byte[] deleteStream;//删除的数据流

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismissMyProgressDialog(a);
            switch (msg.what){

                case SUCCEED_DELETE://删除成功

                    Toast.makeText(a,"图片删除成功",Toast.LENGTH_SHORT).show();

                    if(msg.arg1==1){
                        ivPhoto1.setImageDrawable(null);
                       // ivPhoto1.setEnabled(false);
                        ivDelete1.setVisibility(View.GONE);
                    }else if(msg.arg1==2){
                        ivPhoto2.setImageDrawable(null);
                      //  ivPhoto2.setEnabled(false);
                        ivDelete2.setVisibility(View.GONE);
                    }else if(msg.arg1==3){
                        ivPhoto3.setImageDrawable(null);
                    //    ivPhoto3.setEnabled(false);
                        ivDelete3.setVisibility(View.GONE);
                    }

                    break;

                case PROBLEM://失败
                    Toast.makeText(a,"图片删除失败",Toast.LENGTH_SHORT).show();
                    break;

                case OVERTIME:

                    break;
            }

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getFragmentManager();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_cxsm,container,false);

        isFirst=true;//如果是第一个fragment就给它赋值true，其他的fragment不用管这个变量

        btnPhoto=view.findViewById(R.id.btn_photo_fragment_cxsm);

        btnStart=view.findViewById(R.id.btn_start_fragment_cxsm);


        ivPhoto1=view.findViewById(R.id.iv_cxsm_photo_one);
        ivPhoto2=view.findViewById(R.id.iv_cxsm_photo_two);
        ivPhoto3=view.findViewById(R.id.iv_cxsm_photo_three);

        ivDelete1=view.findViewById(R.id.iv_cxsm_delete_one);
        ivDelete1.setVisibility(View.GONE);

        ivDelete2=view.findViewById(R.id.iv_cxsm_delete_two);
        ivDelete2.setVisibility(View.GONE);

        ivDelete3=view.findViewById(R.id.iv_cxsm_delete_three);
        ivDelete3.setVisibility(View.GONE);

       a=(InvestActivity) getActivity();

        if(TextUtils.equals(a.adminInfo.getPASS_TYPE(),"A类")&&TextUtils.equals(a.adminInfo2.getPASS_TYPE(),"A类")){
            btnStart.setEnabled(false);
            btnPhoto.setEnabled(false);

            ivPhoto1.setEnabled(false);
            ivPhoto2.setEnabled(false);
            ivPhoto3.setEnabled(false);
            ivDelete1.setEnabled(false);
            ivDelete2.setEnabled(false);
            ivDelete3.setEnabled(false);

        }else{
            btnPhoto.setEnabled(true);
            btnStart.setEnabled(true);

            ivPhoto1.setEnabled(true);
            ivPhoto2.setEnabled(true);
            ivPhoto3.setEnabled(true);
            ivDelete1.setEnabled(true);
            ivDelete2.setEnabled(true);
            ivDelete3.setEnabled(true);
        }

        if(a.typeId==2){//已答

            btnStart.setEnabled(false);
            btnPhoto.setEnabled(false);

            ivPhoto1.setEnabled(false);
            ivPhoto2.setEnabled(false);
            ivPhoto3.setEnabled(false);
            ivDelete1.setEnabled(false);
            ivDelete2.setEnabled(false);
            ivDelete3.setEnabled(false);

        }
        return view;
    }



    @Override
    protected void loadData() {

       btnStart.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);

       ivDelete1.setOnClickListener(this);
        ivDelete2.setOnClickListener(this);
        ivDelete3.setOnClickListener(this);

       ivPhoto1.setOnClickListener(this);
        ivPhoto2.setOnClickListener(this);
        ivPhoto3.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent i=new Intent(a, ImgDetailActivity.class);


        deleteStream=new byte[1];


        switch (view.getId()){

            case R.id.btn_start_fragment_cxsm://开始答题

                if(ivPhoto1.getDrawable()!=null&&ivPhoto2.getDrawable()!=null&&ivPhoto3.getDrawable()!=null){
                    a.vp.setCurrentItem(1);

                    a.rbTwo.setChecked(true);

                    btnStart.setVisibility(View.GONE);
                }else{

                    Toast.makeText(a,"请先上传三张图片!",Toast.LENGTH_SHORT).show();

                }


                break;





            case R.id.rl_cxsm_photo_three:

                if(ivPhoto3.getDrawable()!=null) {

                    Toast.makeText(a,"请先删除图片!",Toast.LENGTH_SHORT).show();

                }else{


                    //拍照按钮
                    autoObtainCameraPermission();
                }

                break;

            case R.id.iv_cxsm_delete_one://删除按钮1

              a.imageViewIndex=1;
                showAlertDialog();
              //  deleteImg(deleteStream);


                break;

            case R.id.iv_cxsm_delete_two://删除按钮2

               a.imageViewIndex=2;
                showAlertDialog();
              //  deleteImg(deleteStream);
                break;

            case R.id.iv_cxsm_delete_three://删除按钮3

                a.imageViewIndex=3;
                showAlertDialog();
              //  deleteImg(deleteStream);
                break;

            case R.id.btn_photo_fragment_cxsm://上传图片

             //   ToastUtils.showShort(a, "上传图片");

                break;

            case R.id.iv_cxsm_photo_one://照片1

                if(ivPhoto1.getDrawable()!=null) {
                    i.putExtra("pInfo",a.pInfo);
                    i.putExtra("num",1);
                    a.startActivity(i);

                }else{
                a.imageViewIndex=1;
                    //拍照按钮
                    autoObtainCameraPermission();
                }

                break;

            case R.id.iv_cxsm_photo_two://照片2


                if(ivPhoto2.getDrawable()!=null) {
                    i.putExtra("pInfo",a.pInfo);
                    i.putExtra("num",2);
                    a.startActivity(i);

                }else{
                    a.imageViewIndex=2;
                    //拍照按钮
                    autoObtainCameraPermission();
                }
                break;

            case R.id.iv_cxsm_photo_three://照片3


                if(ivPhoto3.getDrawable()!=null) {
                    i.putExtra("pInfo",a.pInfo);
                    i.putExtra("num",3);
                    a.startActivity(i);

                }else{
                    a.imageViewIndex=3;
                    //拍照按钮
                    autoObtainCameraPermission();
                }
                break;
        }

    }

    /**
     * 自动获取相机权限
     */
    private void autoObtainCameraPermission() {

        if (ContextCompat.checkSelfPermission(a, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(a, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(a, Manifest.permission.CAMERA)) {
                ToastUtils.showShort(a, "您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(a, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, a.CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (PhotoUtils.hasSdcard()) {
                a.imageUri = Uri.fromFile(a.fileUri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    a.imageUri = FileProvider.getUriForFile(a, "com.youli.oldageassess.provider", a.fileUri);//通过FileProvider创建一个content类型的Uri
                PhotoUtils.takePicture(a, a.imageUri, a.CODE_CAMERA_REQUEST);
            } else {
                ToastUtils.showShort(a, "设备没有SD卡！");
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST_CODE: {//调用系统相机申请拍照权限回调
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PhotoUtils.hasSdcard()) {
                        a.imageUri = Uri.fromFile(a.fileUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            a.imageUri = FileProvider.getUriForFile(a, "com.youli.oldageassess.provider", a.fileUri);//通过FileProvider创建一个content类型的Uri
                        PhotoUtils.takePicture(a, a.imageUri, a.CODE_CAMERA_REQUEST);
                    } else {
                        ToastUtils.showShort(a, "设备没有SD卡！");
                    }
                } else {

                    ToastUtils.showShort(a, "请允许打开相机！！");
                }
                break;


            }

        }
    }

    //删除图片
    private void deleteImg(final byte[] shujuliu) {

        showMyProgressDialog(a);

        final HttpClient client = new DefaultHttpClient();

        final String strhttp = MyOkHttpUtils.BaseUrl + "/Json/Set_Img.aspx?SQH=" + a.pInfo.getSQH() + "&Img_No="+a.imageViewIndex;
        Log.e("2018-1-29", "图片url" + strhttp);
        new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        String cookies = SharedPreferencesUtils.getString("cookies");
                        HttpPost post = new HttpPost(strhttp);

                        Message msg=Message.obtain();


                        try {
                            post.setHeader("cookie", cookies);
                            Log.e("2018-1-29", "cookies==" + cookies);
                            if (shujuliu != null) {
                                Log.e("2018-1-29", "图片shujuliu" + new String(shujuliu));
                                ByteArrayEntity arrayEntity = new ByteArrayEntity(shujuliu);
                                arrayEntity.setContentType("application/octet-stream");
                                post.setEntity(arrayEntity);
                            }

                            HttpResponse response = client.execute(post);
                            Log.e("2018-1-29", "图片提交响应码" + response.getStatusLine().getStatusCode());
                            if (response.getStatusLine().getStatusCode() == 200) {

                                HttpEntity entity = response.getEntity();
                                //EntityUtils中的toString()方法转换服务器的响应数据
                                final String str = EntityUtils.toString(entity, "utf-8");

                                Log.e("2018-1-29", "删除图片str" + str);

                                if(TextUtils.equals("True",str)){


                                    msg.arg1=a.imageViewIndex;
                                    msg.what = SUCCEED_DELETE;


                                }else{
                                    msg.what = PROBLEM;
                                }


                            }else {
                                msg.what = PROBLEM;
                            }
                        } catch (Exception e) {
                            msg.what = PROBLEM;

                            e.printStackTrace();
                        } finally {

                            post.abort();
                            mHandler.sendMessage(msg);
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

    private void showAlertDialog(){

        final AlertDialog.Builder builder=new AlertDialog.Builder(a);
        builder.setTitle("温馨提示");
        builder.setMessage("您确定要删除图片吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImg(deleteStream);

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
