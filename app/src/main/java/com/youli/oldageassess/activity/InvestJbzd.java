package com.youli.oldageassess.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.youli.oldageassess.R;
import com.youli.oldageassess.entity.AnswerInfo;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.SharedPreferencesUtils;
import com.youli.oldageassess.utils.TextViewUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 2018/1/22.
 *
 * 疾病诊断界面
 */

public class InvestJbzd extends BaseActivity implements View.OnClickListener{


    private final int SUCCEED_LAST = 10000;//上一题的提交
    private final int SUCCEED_NEXT = 10001;//下一题的提交
    private final int SUCCEED_ALL = 10002;//提交大标题
    private final int PROBLEM = 10003;//提交失败

    private Context mContext=this;

    private List<InvestInfo>  InvestInfoList=new ArrayList<>();//问卷所有的信息
    private int typeId;//最上层的ID(COPD患者)
    private LinearLayout llInvest;

    private int index = 0;
    private Button btnLast,btnNext,btnFinsh;//上一题的按钮，下一题的按钮,完成返回上一级
    private TextView tvTitle;//最上面的标题

    private List<InvestInfo>  twoInfoList=new ArrayList<>();//第二层（1.局部症状）
    private List<InvestInfo>  threeInfoList=new ArrayList<>();//第三层（慢性咳嗽）
    private List<InvestInfo>  fourInfoList=new ArrayList<>();//第四层（偶尔咳嗽）
    private InvestInfo currentInfo;// 用于表示当前题
    private List<RadioButton> radioButtons = new ArrayList<RadioButton>();//单选题
    // 当前控件
    private List<Object> CurrCol = new ArrayList<Object>();

    private PersonInfo personInfo;//个人信息

    private byte[] shujuliu;//答案数据流

    private List<AnswerInfo> allAnswerList=new ArrayList<>();//所有答案的数据

    public static HashMap<Integer,List<String>> deleteList=new HashMap<>();//用来删题的id
    public static List<String> list=new ArrayList<>();

    private boolean isLast;//判断是否是最后一题

    private ProgressDialog pd;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            dismissMyProgressDialog();

            switch (msg.what) {

                case SUCCEED_LAST://上一题的提交
                    isLast=false;
                    btnLast.setEnabled(true);
                    index--;
                    drawLayout(llInvest,threeInfoList.get(index));
                    Log.e("2018-1-23","上一题的提交成功");
                    break;
                case SUCCEED_NEXT://下一题的提交
                    btnNext.setEnabled(true);
                    btnLast.setVisibility(View.VISIBLE);
                    Log.e("2018-1-23","下一题的提交成功");
                    if (index == threeInfoList.size() - 1) {
                        isLast=true;
                        btnNext.setEnabled(false);
                        showLastDialog("last");
                        btnFinsh.setVisibility(View.VISIBLE);
                        return;
                    }
                    index++;
                    currentInfo=threeInfoList.get(index);
                    drawLayout(llInvest,threeInfoList.get(index));

                    break;
                case SUCCEED_ALL://提交大标题
                    Intent intent=new Intent();
                    intent.putExtra("answerList", (Serializable) allAnswerList);
                    intent.putExtra("Title", (Serializable) tvTitle.getText().toString());

                    setResult(0,intent);
                    finish();
                    break;
                case PROBLEM://提交失败

                   btnNext.setEnabled(true);
                    btnLast.setEnabled(true);
                    btnFinsh.setEnabled(true);

                    if(TextUtils.equals(msg.obj+"","上一题的提交")){
                        btnFinsh.setVisibility(View.GONE);
                    }else
                    if(TextUtils.equals(msg.obj+"","下一题的提交")){
                        btnFinsh.setVisibility(View.GONE);
                    }else if(TextUtils.equals(msg.obj+"","提交大标题")){
                        btnFinsh.setVisibility(View.VISIBLE);
                    }


                    break;

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_jbzd);

        tvTitle=findViewById(R.id.tv_invest_jbzd_title);

        personInfo=(PersonInfo) getIntent().getSerializableExtra("personInfo");

        tvTitle.setText(getIntent().getStringExtra("title"));//最上面的标题

        btnLast=findViewById(R.id.btn_last);
        btnLast.setOnClickListener(this);

        btnNext=findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

        btnFinsh=findViewById(R.id.btn_finsh);
        btnFinsh.setOnClickListener(this);

        llInvest = findViewById(R.id.jbzd_ll);

      //  InvestInfoList = (List<InvestInfo>) getIntent().getSerializableExtra("info");
        InvestInfoList=PersonListActivity.investInfo;
        typeId = getIntent().getIntExtra("id", 0);


        for(InvestInfo allInfo:InvestInfoList){//得到第二层数据

            if(allInfo.getPARENT_ID()==typeId){

                twoInfoList.add(allInfo);

            }

        }

        for(InvestInfo allInfo:InvestInfoList){//得到第三层数据

        for(InvestInfo tInfo:twoInfoList) {

            if (allInfo.getPARENT_ID() == tInfo.getID()) {

                threeInfoList.add(allInfo);

            }

        }

        for(InvestInfo fInfo:threeInfoList){

            if (allInfo.getPARENT_ID() == fInfo.getID()) {

                fourInfoList.add(allInfo);

            }

        }

        }


             if(deleteList.get(typeId)!=null) {
                 Log.e("2018-1-23", "delList=" + deleteList.get(typeId).size());
             }


       drawLayout(llInvest,threeInfoList.get(index));

    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_last://上一题

                btnFinsh.setVisibility(View.GONE);
                btnNext.setEnabled(true);

                if (index == 0) {
                    showLastDialog("first");
                    return;
                }


                                     // 去掉单选按钮
                for(InvestInfo fourList:fourInfoList){
                                    for (RadioButton radioButton : radioButtons) {

                                        if(fourList.getPARENT_ID()==threeInfoList.get(index).getID()&&fourList.getID()==radioButton.getId()) {
                                            radioButton.setChecked(false);
                                        }
                                    }
                    }
                btnLast.setEnabled(false);
                showMyProgressDialog(this);
                submitCom("上一题的提交");
//                index--;
//                drawLayout(llInvest,threeInfoList.get(index));
                break;

            case R.id.btn_next://下一题

                btnLast.setVisibility(View.VISIBLE);
                InvestInfo info = threeInfoList.get(index);

                if ( !checkRadioIsChecked(fourInfoList, info.getID())) {//这个if里面是用来判断二级单选是否做完
                    Toast.makeText(this, "答案不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<AnswerInfo> list = new ArrayList<>();
                list = getAnswerInfo(info);
                allAnswerList.addAll(list);
                String answerString = parseAnswerInfo(list);

                Log.e("2018-1-23","answerString=="+answerString);

                try {
                    shujuliu = answerString.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                btnNext.setEnabled(false);
                showMyProgressDialog(this);
                submitCom("下一题的提交");

                break;


            case R.id.btn_finsh:

                List<AnswerInfo> answerList = new ArrayList<>();
                answerList.add(new AnswerInfo(typeId,typeId,""));
                String titleString = parseAnswerInfo(answerList);
                Log.e("2018-1-23","titleString=="+titleString);

                try {
                    shujuliu = titleString.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                btnFinsh.setEnabled(false);
                submitCom("提交大标题");
                break;

        }

    }

    private void drawLayout(LinearLayout bigLl,InvestInfo threeInfo){//画布局
        CurrCol.clear();
        bigLl.removeAllViews();

        LinearLayout allLl=new LinearLayout(this);//问题和选项的布局
        allLl.setOrientation(LinearLayout.VERTICAL);

        for(InvestInfo twoInfos:twoInfoList){//这个里面是画第二级的标题

            if(twoInfos.getID()==threeInfo.getPARENT_ID()){

                TextView twoTv=new TextView(this);
                twoTv.setText(twoInfos.getTITLE_L());
                twoTv.setTextSize(22);
                twoTv.setTextColor(Color.parseColor("#000000"));
                twoTv.setPadding(20,0,0,0);
                allLl.addView(twoTv);

                break;
            }

        }

        TextView threeTv=new TextView(this);
        threeTv.setTextSize(22);
        threeTv.setTextColor(Color.parseColor("#000000"));
        threeTv.setPadding(20,0,0,0);
        threeTv.setText(threeInfo.getTITLE_L());//这个里面是画第三级的标题

        allLl.addView(threeTv);
  //===============================

        LinearLayout optionLl=new LinearLayout(this);//选项和按钮的布局
        optionLl.setOrientation(LinearLayout.HORIZONTAL);

        RadioGroup radioGroup = new RadioGroup(this);


        LinearLayout qLl=new LinearLayout(this);//选项文本的布局
        qLl.setOrientation(LinearLayout.VERTICAL);

                for(InvestInfo fInfo:fourInfoList) {
                if(threeInfo.getID()==fInfo.getPARENT_ID()){

                    drawOptionLayout(radioGroup,qLl,fInfo,threeInfo);

                }

            }

        optionLl.addView(radioGroup);
        optionLl.addView(qLl);
        allLl.addView(optionLl);
        bigLl.addView(allLl);
    }

    //画选项的布局
    private void drawOptionLayout(RadioGroup rg,LinearLayout optionLl,InvestInfo info,InvestInfo threeInfo){


        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                80);


        if(threeInfo.getINPUT_TYPE().equals("单选")){

            RadioButton rb=new RadioButton(this);
            rb.setId(info.getID());
            rb.setLayoutParams(lp);
            CurrCol.add(rb);
            rg.addView(rb);

            if (radioButtons.size() > 0) {
                List<RadioButton> tempRadioButtons = new ArrayList<>();
                for (RadioButton radioButton2 : radioButtons) {
                    if (radioButton2.getId() == info.getID() && radioButton2.isChecked()) {
                        rb.setChecked(true);
                        tempRadioButtons.add(radioButton2);
                    }
                }
                radioButtons.removeAll(tempRadioButtons);
                tempRadioButtons.clear();
            }
            radioButtons.add(rb);
        }

        LinearLayout contentLl=new LinearLayout(this);//问题和选项的布局
        contentLl.setOrientation(LinearLayout.VERTICAL);

         TextView optionTv=new TextView(this);
         if(info.getCODE()!=null) {
             optionTv.setText(info.getCODE()+info.getTITLE_L());
         }else{
             optionTv.setText(info.getTITLE_L());
         }
         optionTv.setGravity(Gravity.CENTER);
        optionTv.setLayoutParams(lp);
        contentLl.addView(optionTv);

        optionLl.addView(contentLl);
    }


    //获取单条数据
    private List<AnswerInfo> getAnswerInfo(InvestInfo answer) {

        List<AnswerInfo> list = new ArrayList<AnswerInfo>();

        AnswerInfo answerInfo = null;

        if (CurrCol.size() > 0) {

            for (Object col : CurrCol) {

                if (col instanceof RadioButton) {

                    RadioButton radioButton = (RadioButton) col;

                    if (radioButton.isChecked()) {
                        answerInfo = new AnswerInfo();
                        answerInfo.setAnswerId(radioButton.getId());
                        answerInfo.setAnswerNo(answer.getID());//2018-01-17这里可能出错
                        answerInfo.setAnswerText("");
                        list.add(answerInfo);
                    }


                }
            }


        }
        return list;
    }

    private String parseAnswerInfo(List<AnswerInfo> answerInfos) {

        JSONArray array = new JSONArray();
        if (answerInfos.size() > 0) {
            for (AnswerInfo answerInfo : answerInfos) {
                JSONObject object = new JSONObject();
                try {
                    object.put("DETIL_ID", answerInfo.getAnswerId());
                    object.put("INPUT_VALUE", answerInfo.getAnswerText());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                array.put(object);
            }
        }

        return array.toString();

    }

    //提交答案
    private void submitCom(String type) {

        submitAnswer(type, shujuliu, personInfo.getSQH());

    }

    private void submitAnswer(final String type, final byte[] shujuliu, String sqh) {

        final HttpClient client = new DefaultHttpClient();
        String strhttp=null;
        if(TextUtils.equals(type, "上一题的提交")&&!isLast){//不是最后一题
            strhttp = MyOkHttpUtils.BaseUrl + "/Json/Set_Qa_Receiv_Special.aspx?SQH=" + sqh + "&master_id=1&del=true&Receiv_id="+deleteList.get(typeId).get(list.size()-1);
        }else if(TextUtils.equals(type, "上一题的提交")&&isLast){//是最后一题
            strhttp = MyOkHttpUtils.BaseUrl + "/Json/Set_Qa_Receiv_Special.aspx?SQH=" + sqh + "&master_id=1&del=true&Receiv_id="+deleteList.get(typeId).get(list.size()-1)+","+deleteList.get(typeId).get(list.size()-2);
        }else{
            strhttp = MyOkHttpUtils.BaseUrl + "/Json/Set_Qa_Receiv_Special.aspx?SQH=" + sqh + "&master_id=1";
        }
        Log.e("2018-1-23", "企业提交url" + strhttp);
        final String finalStrhttp = strhttp;
        new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        String cookies = SharedPreferencesUtils.getString("cookies");
                        HttpPost post = new HttpPost(finalStrhttp);

                        Message msg = Message.obtain();

                        try {
                            post.setHeader("cookie", cookies);
                            if (!TextUtils.equals(type, "上一题的提交")&&shujuliu != null) {
                                Log.e("2018-1-23", "企业提交shujuliu" + new String(shujuliu));
                                ByteArrayEntity arrayEntity = new ByteArrayEntity(shujuliu);
                                arrayEntity.setContentType("application/octet-stream");
                                post.setEntity(arrayEntity);
                            }

                            HttpResponse response = client.execute(post);
                            Log.e("2018-1-23", "企业提交响应码" + response.getStatusLine().getStatusCode());
                            if (response.getStatusLine().getStatusCode() == 200) {

                                HttpEntity entity = response.getEntity();
                                //EntityUtils中的toString()方法转换服务器的响应数据
                                final String str = EntityUtils.toString(entity, "utf-8");

                                Log.e("2018-1-23", "企业提交str" + str);


                                if (TextViewUtils.firstIsNumber(str)&&TextUtils.equals(type, "下一题的提交")) {
                                    list.add(str);
                                    deleteList.put(typeId,list);
                                    Log.e("2018-1-23", "下一题++++++++++++" + list.size());
                                        msg.obj = str;
                                        msg.what = SUCCEED_NEXT;
                                }else if(TextViewUtils.firstIsNumber(str)&&TextUtils.equals(type,"提交大标题")) {
                                    list.add(str);
                                    deleteList.put(typeId,list);
                                    msg.obj = str;
                                    msg.what = SUCCEED_ALL;

                                }else if(TextUtils.equals(type, "上一题的提交")&&TextUtils.equals("True",str)){
                                    if(isLast) {//是最后一题的话，要删除两条
                                        list.remove(deleteList.get(typeId).get(list.size() - 1));
                                        list.remove(deleteList.get(typeId).get(list.size() - 1));
                                    }else{//不是最后一题的话，只要删除一条
                                        list.remove(deleteList.get(typeId).get(list.size() - 1));
                                    }
                                    Log.e("2018-1-23", "上一题--------------------" + list.size());
                                    Log.e("2018-1-23", "上一题的提交str" + str);
                                    msg.obj = str;
                                    msg.what = SUCCEED_LAST;


                                } else {

                                    msg.obj=type;
                                    msg.what = PROBLEM;
                                }

                            }
                        } catch (Exception e) {
                            msg.obj=type;
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

    @Override
    public void onBackPressed() {
        showAlertDialog();
    }


    private void showAlertDialog(){

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("您确定放弃答题吗?");
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

    private void showLastDialog(String mark){

        String contentStr = null;
        
        if(TextUtils.equals(mark,"last")){
            contentStr="当前已经是最后一题了！";
        } else if (TextUtils.equals(mark, "first")) {
            contentStr="当前已经是第一题了！";
        }
        
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("温馨提示");
        builder.setMessage(contentStr);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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

    private void dismissMyProgressDialog(){

        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
            pd=null;
        }

    }

    private boolean checkRadioIsChecked(List<InvestInfo> infos, int questionId) {

        for (RadioButton radioButton : radioButtons) {

            for (InvestInfo investInfo : infos) {

                if (radioButton.getId() == investInfo.getID() && investInfo.getPARENT_ID() == questionId
                        && radioButton.isChecked()) {

                    return true;

                }
            }
        }

        return false;
    }

}
