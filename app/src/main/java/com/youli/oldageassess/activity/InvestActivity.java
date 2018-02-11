package com.youli.oldageassess.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.youli.oldageassess.R;
import com.youli.oldageassess.adapter.InvestPageFragmentAdapter;
import com.youli.oldageassess.entity.AdminInfo;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.entity.ResultInfo;
import com.youli.oldageassess.fragment.CxsmFragment;
import com.youli.oldageassess.fragment.JbxmFragment;
import com.youli.oldageassess.fragment.JbzdFragment;
import com.youli.oldageassess.fragment.JtztFragment;
import com.youli.oldageassess.fragment.MyBaseFragment;
import com.youli.oldageassess.fragment.ZtzkFragment;
import com.youli.oldageassess.utils.BitmapUtils;
import com.youli.oldageassess.utils.IOUtil;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.PhotoUtils;
import com.youli.oldageassess.utils.ProgressDialogUtils;
import com.youli.oldageassess.utils.SharedPreferencesUtils;
import com.youli.oldageassess.utils.TextViewUtils;
import com.youli.oldageassess.utils.ToastUtils;
import com.youli.oldageassess.view.MyViewPager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Response;


/**
 * Created by liutao on 2018/1/13.
 *
 * 问卷调查
 */

public class InvestActivity extends FragmentActivity {

    private Context mContext=this;

    private ImageView ivBack;

  //  private String [] title={"诚信声明","家庭状态","基本项目","总体状况","疾病诊断"};

    private final int SUCCEED_PHOTO1=10001;
    private final int SUCCEED_PHOTO2=10002;
    private final int SUCCEED_PHOTO3=10003;
    private final int  PROBLEM=10004;
    private final int OVERTIME=10005;//登录超时

    public  final int CODE_GALLERY_REQUEST = 0xa0;
    public  final int CODE_CAMERA_REQUEST = 0xa1;
    public  final int CODE_RESULT_REQUEST = 0xa2;
    public  final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private Uri cropImageUri;
    public File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    public Uri imageUri;

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

    public List<ResultInfo> resultInfo=new ArrayList<>();

    public int typeId;//他等于1时是未答，他等于2时是已答，
    public AdminInfo adminInfo,adminInfo2;//操作员信息
    public MyViewPager vp;
    public int imageViewIndex=1;//照片的索引（1到3） 默认是1；

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){

                case SUCCEED_PHOTO1://照片1

                   if(msg.obj!=null) {
                      // cxsmF.ivPhoto1.setEnabled(true);
                       cxsmF.ivPhoto1.setImageBitmap((Bitmap) msg.obj);
                       cxsmF.ivDelete1.setVisibility(View.VISIBLE);
                     //  imageViewIndex = 2;
                   }else{
                     //  imageViewIndex = 1;
                     //  cxsmF.ivPhoto1.setEnabled(false);
                       cxsmF.ivDelete1.setVisibility(View.GONE);
                   }
                    getPic("two");//照片2
                    break;

                case SUCCEED_PHOTO2://照片2
                    if(msg.obj!=null) {
                      //  cxsmF.ivPhoto2.setEnabled(true);
                        cxsmF.ivPhoto2.setImageBitmap((Bitmap) msg.obj);
                        cxsmF.ivDelete2.setVisibility(View.VISIBLE);
                    //    imageViewIndex = 3;
                    }else{
                     //   imageViewIndex = 2;
                  //      cxsmF.ivPhoto2.setEnabled(false);
                        cxsmF.ivDelete2.setVisibility(View.GONE);
                    }
                    getPic("three");//照片3
                    break;

                case SUCCEED_PHOTO3://照片3
                    if(msg.obj!=null) {
                      //  imageViewIndex = 1;
                     //   cxsmF.ivPhoto3.setEnabled(true);
                        cxsmF.ivPhoto3.setImageBitmap((Bitmap) msg.obj);
                        cxsmF.ivDelete3.setVisibility(View.VISIBLE);
                    }else{
                    //    imageViewIndex = 3;
                     //   cxsmF.ivPhoto3.setEnabled(false);
                        cxsmF.ivDelete3.setVisibility(View.GONE);
                    }
                    break;

                case PROBLEM:

                    break;

                case OVERTIME://登录超时

                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest);

        pInfo=(PersonInfo)getIntent().getSerializableExtra("pInfo");
        adminInfo=(AdminInfo)getIntent().getSerializableExtra("adminInfo");
        adminInfo2=(AdminInfo)getIntent().getSerializableExtra("adminInfo2");
        typeId=getIntent().getIntExtra("type",0);

        resultInfo=(List<ResultInfo>)getIntent().getSerializableExtra("resultData");

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
     //  if(info.getTYPE_ID()==2){

                jtztList.add(info);

    //     }

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


        if(typeId==1) {
            vp.setCurrentItem(0);
        }else{
            vp.setCurrentItem(1);
        }
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               ProgressDialogUtils.dismissMyProgressDialog(mContext);
           }
       },1500);

        getPic("one");//照片1

    }

    private void  getPic(final String mark){

        // http://web.youli.pw:8088/Json/GetStaffPic.aspx?staff=1
        new Thread(

                new Runnable() {
                    @Override
                    public void run() {

                      //  http://183.194.4.58:81/Json/Get_Img.aspx?SQH=0013CH101201801020002&sl_No=1
                        String urlPic = null;
                        if(TextUtils.equals(mark,"one")) {
                             urlPic = MyOkHttpUtils.BaseUrl + "/Json/Get_Img.aspx?SQH=" + pInfo.getSQH() + "&sl_No="+1;
                        }else  if(TextUtils.equals(mark,"two")) {
                            urlPic = MyOkHttpUtils.BaseUrl + "/Json/Get_Img.aspx?SQH=" + pInfo.getSQH() + "&sl_No="+2;
                        }else  if(TextUtils.equals(mark,"three")) {
                            urlPic = MyOkHttpUtils.BaseUrl + "/Json/Get_Img.aspx?SQH=" + pInfo.getSQH() + "&sl_No="+3;
                        }

                        Log.e("2018-1-29","缩略图=="+urlPic);

                        Response response = MyOkHttpUtils.okHttpGet(urlPic);
                        try {
                            Message msg = Message.obtain();

                            if (response != null) {
                                InputStream is = response.body().byteStream();

                                byte[] picData = IOUtil.getBytesByStream(is);
                                Bitmap btp = BitmapFactory.decodeByteArray(picData, 0, picData.length);

                                msg.obj = btp;
                                if(TextUtils.equals(mark,"one")) {
                                    msg.what = SUCCEED_PHOTO1;
                                }else if(TextUtils.equals(mark,"two")) {
                                    msg.what = SUCCEED_PHOTO2;
                                }else if(TextUtils.equals(mark,"three")) {
                                    msg.what = SUCCEED_PHOTO3;
                                }
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

        if (data != null) {
            String title = data.getStringExtra("Title");
            if (map.get(title) != null) {
                map.get(title).setText(title + "(完成)");
                map.get(title).setEnabled(false);
            }
        }

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                    cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, CODE_RESULT_REQUEST);
                    break;
                case CODE_GALLERY_REQUEST://访问相册完成回调
                    if (PhotoUtils.hasSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(mContext, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            newUri = FileProvider.getUriForFile(mContext, "com.youli.oldageassess.provider", new File(newUri.getPath()));
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, CODE_RESULT_REQUEST);
                    } else {
                        ToastUtils.showShort(mContext, "设备没有SD卡！");
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    if (bitmap != null) {
                        showImages(bitmap);
                    }
                    break;

                    default:

                        break;
            }
        }

    }


    private void showImages(Bitmap bitmap) {

        try {
            deleteFile(fileUri);//删除原图
            formatFileSize(getFileSize(fileCropUri));//计算裁剪之后图片的大小

            File     compressFile = BitmapUtils.compressImage2(bitmap);//计算压缩之后图片的大小
            deleteFile(fileCropUri);//删除裁剪的图
            getBytes(compressFile.getPath(),compressFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获得指定文件的byte数组(文件转化为字节数组)
     */
    private byte[] getBytes(String filePath,File     compressFile){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
            uploadImg(buffer,compressFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return buffer;
    }



    //上传单个图片
    private void uploadImg(final byte[] shujuliu, final File     compressFile) {

        final HttpClient client = new DefaultHttpClient();

        final String strhttp = MyOkHttpUtils.BaseUrl + "/Json/Set_Img.aspx?SQH=" + pInfo.getSQH() + "&Img_No="+imageViewIndex;
        Log.e("2018-1-29", "图片url" + strhttp);
        new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        String cookies = SharedPreferencesUtils.getString("cookies");
                        HttpPost post = new HttpPost(strhttp);

                        Message msg = Message.obtain();

                        try {
                            post.setHeader("cookie", cookies);
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

                                Log.e("2018-1-29", "图片str" + str);

                                 if(TextUtils.equals("True",str)){

                                     deleteFile(compressFile);

                                     getPic("one");

                                 }

                            }
                        } catch (Exception e) {
                           // msg.what = PROBLEM;
                            e.printStackTrace();
                        } finally {
                            post.abort();
                            //mHandler.sendMessage(msg);
                        }

                    }
                }

        ).start();

    }





    /**
     * 读取文件的大小
     */
    public void deleteFile(File f) throws Exception{

        if (f.exists()) {
            f.delete();
        }
    }

    /**
     * 读取文件的大小
     */
    public long getFileSize(File f) throws Exception{
        long l = 0;
        if (f.exists()){
            FileInputStream mFIS = new FileInputStream(f);
            l= mFIS.available();
        } else {
            f.createNewFile();
        }
        return l;
    }

    /**
     * 将文件大小转换成字节
     */
    public String formatFileSize(long fSize){
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if(fSize<1024){
            fileSizeString = df.format((double) fSize) + "B";
        } else if ( fSize >104875){
            fileSizeString = df.format((double) fSize/1024) + "K";
        } else if ( fSize >1073741824){
            fileSizeString = df.format((double) fSize/104875 ) + "M";
        } else {
            fileSizeString = df.format((double) fSize/1073741824) + "G";
        }

        Log.e("2018-1-28","fileSizeString=="+fileSizeString);

        return fileSizeString;
    }


}
