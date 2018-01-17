package com.youli.oldageassess.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.youli.oldageassess.activity.InvestActivity;
import com.youli.oldageassess.entity.AnswerInfo;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.SharedPreferencesUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liutao on 2018/1/13.
 *
 * 家庭状态
 */

public class JtztFragment extends MyBaseFragment implements View.OnClickListener{

    private FragmentManager fm;


    private JtztFragment jtztF;
    private JbxmFragment jbxmF;


    private View view;

    private Button btnLast,btnNext,btnAll,btnRestart,btnSubmit;

    private LinearLayout llInvest;//所有问卷的信息的布局

    private PersonInfo personInfo;//个人信息

    private List<InvestInfo> InvestInfo;//所有问卷的信息

    private InvestInfo currentInfo;// 用于表示当前题
    // 当前控件
    private List<Object> CurrCol = new ArrayList<Object>();

    private List<InvestInfo> questionInfos;//问题的集合，没有选项

    private List<InvestInfo> answerInfo = new ArrayList<InvestInfo>();//二级选项的集合

    private List<InvestInfo> answerThirdInfo = new ArrayList<InvestInfo>();//三级选项的集合

    // 保存大题的编辑框
    private List<EditText> questionEditTexts = new ArrayList<EditText>();

    private int index = 0;

    private byte [] shujuliu;//答案数据流

    public static final JtztFragment newInstance(List<InvestInfo> info, PersonInfo pInfo){

        JtztFragment fragment=new JtztFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("investInfo",(Serializable)info);
        bundle.putSerializable("personInfo",pInfo);
        fragment.setArguments(bundle);

        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm=getFragmentManager();

        InvestInfo=(List<InvestInfo>) getArguments().getSerializable("investInfo");

        personInfo=(PersonInfo)getArguments().getSerializable("personInfo");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_jtzt,container,false);



        return view;
    }


    @Override
    protected void loadData() {

        //Log.e("2018-1-13","============长度="+info.size());

        initViews();
    }

    private void initViews(){

        llInvest=view.findViewById(R.id.ll);

        btnLast=view.findViewById(R.id.btn_last);
        btnLast.setOnClickListener(this);
        btnNext=view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        btnAll=view.findViewById(R.id.btn_all);
        btnAll.setOnClickListener(this);
        btnRestart=view.findViewById(R.id.btn_restart);
        btnRestart.setOnClickListener(this);
        btnSubmit=view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        showFirst();//默认显示第一题
    }

    private void showFirst(){

        if(InvestInfo.size()>0){

            questionInfos=getQuestionByParent();
            if (questionInfos.size() > 0) {

                index=0;
                fretchTree(llInvest, questionInfos.get(index), "");
            }
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_last://上一题

                btnAll.setVisibility(View.GONE);

            if(index==0){
                Toast.makeText(getActivity(), "已经是第一题了", Toast.LENGTH_SHORT).show();
                return;
            }

            if(index==1){
                btnLast.setVisibility(View.GONE);
            }
                index--;
                currentInfo=questionInfos.get(index);
                fretchTree(llInvest, currentInfo, "");
                break;

            case R.id.btn_next://下一题

                InvestInfo info=questionInfos.get(index);

                List<InvestInfo> tempSmallWenJuan=getAnswerByParentId(info);//选项

                int questionNo=info.getID();

                if(questionEditTexts.size()>0){//这个if里面是用来判断一级标题里面的填空是否做完

                    for(EditText editText:questionEditTexts){

                        if (editText.getId() == questionNo
                                && "".equals(editText.getText().toString()
                                .trim())) {
                            Toast.makeText(getActivity(), "答案不能为空!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                }

               if(index==questionInfos.size()-1){
                   Toast.makeText(getActivity(),"已经是最后一题了",Toast.LENGTH_SHORT).show();
                   btnAll.setVisibility(View.VISIBLE);
                   return;
               }


                List<AnswerInfo> list = getAnswerInfo(questionNo, info);
                String answerString = parseAnswerInfo(list);

                Log.e("2018-1-16","answerString="+answerString);
                shujuliu= answerString.getBytes();
                submitCom();
                btnLast.setVisibility(View.VISIBLE);

                index++;
                currentInfo=questionInfos.get(index);
                fretchTree(llInvest, currentInfo, "");

                break;

            case R.id.btn_all://查看全部
             btnRestart.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);

            btnAll.setVisibility(View.GONE);
                btnLast.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);

               llInvest.removeAllViews();
                for (InvestInfo answerInfo : questionInfos) {

                    fretchTree(llInvest, answerInfo, "all");
                }



                break;

            case R.id.btn_restart://重新开始

              //  Toast.makeText(getActivity(),"重新开始",Toast.LENGTH_SHORT).show();

              //  showFirst();//默认显示第一题

                showAlertDialog("restart");



                break;

            case R.id.btn_submit://提交

               //fm.beginTransaction().hide(jbzdF).hide(ztzkF).hide(cxsmF).hide(jtztF).show(jbxmF).commit();

                Toast.makeText(getActivity(),"提交",Toast.LENGTH_SHORT).show();
                fm.beginTransaction().hide(((InvestActivity)getActivity()).jtztF).show(((InvestActivity)getActivity()).jbxmF).commit();

                ((InvestActivity)getActivity()).rbThree.setChecked(true);

                break;
        }

    }

    //搭建布局
    private void fretchTree(LinearLayout layout,InvestInfo info,String isAll){

        if("".equals(isAll)){
            llInvest.removeAllViews();
        }

        CurrCol.clear();

        LinearLayout allLl=new LinearLayout(getActivity());//整体布局（包括问题和选项）
        allLl.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams allparam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        allLl.setLayoutParams(allparam);


        if(!TextUtils.equals("0",info.getTITLE_TOP())&&!TextUtils.equals(null,info.getTITLE_TOP())) {
            TextView topTv = new TextView(getActivity());
            topTv.setText(info.getTITLE_TOP());
            topTv.setTextColor(Color.parseColor("#000000"));
            topTv.setTextSize(18);
            allLl.addView(topTv, allparam);
        }

        LinearLayout qLl=new LinearLayout(getActivity());//问题的布局
        qLl.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvLeft=new TextView(getActivity());//问题左边的部分
        tvLeft.setText(info.getTITLE_L());
        tvLeft.setTextColor(Color.parseColor("#000000"));
        tvLeft.setTextSize(18);
        qLl.addView(tvLeft,allparam);

        if(info.isINPUT()){

            EditText et=new EditText(getActivity());
            et.setId(info.getID());
            CurrCol.add(et);
            et.setPadding(0,-20,0,0);
            if(TextUtils.equals("数字",info.getINPUT_TYPE())){
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            LinearLayout.LayoutParams etParams= new LinearLayout.LayoutParams(info.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);
            qLl.addView(et);

            if(questionEditTexts.size()>0){

                List<EditText> tempEditTexts=new ArrayList<>();
                for(EditText editText2:questionEditTexts){

                    if(editText2.getId()==info.getID()){

                     et.setText(editText2.getText());

                     tempEditTexts.add(editText2);
                    }

                }
                questionEditTexts.removeAll(tempEditTexts);
                tempEditTexts.clear();
            }
            questionEditTexts.add(et);
        }

        allLl.addView(qLl,allparam);
        //上面的代码是在弄问题的布局



        //下面的代码是在弄选项的布局
        LinearLayout aLl=new LinearLayout(getActivity());
        aLl.setOrientation(LinearLayout.HORIZONTAL);
        aLl.setLayoutParams(allparam);

        RadioGroup radioGroup=new RadioGroup(getActivity());
        radioGroup.setLayoutParams(allparam);

        LinearLayout optionLl = new LinearLayout(getActivity());
        optionLl.setLayoutParams(allparam);
        optionLl.setOrientation(LinearLayout.VERTICAL);

        answerInfo = getAnswerByParentId(info);//用问题的信息得到选项的信息

        if(TextUtils.equals("多选",info.getINPUT_TYPE())){//多选

            //多选题
            List<CheckBox> CheckBoxGroup = new ArrayList<CheckBox>();

            for (InvestInfo wenJuanInfo : answerInfo) {
                fretchTreeByQuestionMultiSelect(CheckBoxGroup, radioGroup,
                        wenJuanInfo, optionLl, isAll);
            }

        }else if(TextUtils.equals("单选",info.getINPUT_TYPE())){//单选(还没完成)

            for (InvestInfo wenJuanInfo : answerInfo) {
                fretchTreeByQuestion(radioGroup, wenJuanInfo,
                        optionLl, isAll);
            }

        }else if(TextUtils.equals("数字",info.getINPUT_TYPE())){
            for (InvestInfo wenJuanInfo : answerInfo) {
                fretchTreeByQuestionShuzi(wenJuanInfo,
                        optionLl, isAll);
            }
        }else if(TextUtils.equals("无",info.getINPUT_TYPE())){//第二级

            for (InvestInfo wenJuanInfo : answerInfo) {
                fretchTreeByQuestionTwo(wenJuanInfo,
                        optionLl, isAll);
            }

        }

        aLl.addView(radioGroup,allparam);
        aLl.addView(optionLl,allparam);
        allLl.addView(aLl,allparam);

        layout.addView(allLl,allparam);
    }

    private List<InvestInfo> getQuestionByParent(){

        questionInfos=new ArrayList<InvestInfo>();
        for(InvestInfo info:InvestInfo){

            if(info.getPARENT_ID()==0){
                //PARENT_ID=0就是问题，否则是选项
                questionInfos.add(info);
            }

        }

        return questionInfos;

    }


    //用问题的信息得到选项的信息
    private List<InvestInfo> getAnswerByParentId(InvestInfo info) {
        List<InvestInfo> anwserInfos = new ArrayList<InvestInfo>();
        for (InvestInfo investInfo : InvestInfo) {
            if (investInfo.getPARENT_ID() == info.getID()) {
                anwserInfos.add(investInfo);
            }
        }
        return anwserInfos;
    }

    //得到第三级选项的信息
    private List<InvestInfo> getThirdBySecondId(InvestInfo info) {

        List<InvestInfo> anwserInfos = new ArrayList<InvestInfo>();
        for (InvestInfo investInfo : InvestInfo) {
            if (investInfo.getPARENT_ID() == info.getID()) {
                anwserInfos.add(investInfo);
            }
        }

        return anwserInfos;
    }

    //多选题选项的布局
    private void fretchTreeByQuestionMultiSelect(List<CheckBox> cbGroup,RadioGroup group, InvestInfo wenJuanInfo,
                                                 LinearLayout optionLl, String isAll){

       LinearLayout ll=new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                70);

        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox cb=new CheckBox(getActivity());
        cb.setId(wenJuanInfo.getID());
        CurrCol.add(cb);
        cb.setLayoutParams(lp);
        group.addView(cb);

            //多选题选项的文字和输入框
            TextView tvLeft=new TextView(getActivity());
            tvLeft.setGravity(Gravity.CENTER);
            tvLeft.setLayoutParams(lp);
            tvLeft.setText(wenJuanInfo.getTITLE_L());
            ll.addView(tvLeft,lp);

           if(wenJuanInfo.isINPUT()){

               EditText et=new EditText(getActivity());
               et.setId(wenJuanInfo.getID());
               et.setPadding(0,-20,0,0);
               if(TextUtils.equals("数字",wenJuanInfo.getINPUT_TYPE())){
                   et.setInputType(InputType.TYPE_CLASS_NUMBER);
               }
               LinearLayout.LayoutParams etParams= new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);

               et.setGravity(Gravity.CENTER);
               et.setLayoutParams(etParams);

               CurrCol.add(et);
               ll.addView(et);

               if(!TextUtils.equals("",wenJuanInfo.getTITLE_R())){

                   TextView tvRight=new TextView(getActivity());
                   tvRight.setGravity(Gravity.CENTER);
                   tvRight.setLayoutParams(lp);
                   tvRight.setText(wenJuanInfo.getTITLE_R());
                   ll.addView(tvRight,lp);

               }
         }


        optionLl.addView(ll,lp);
    }


    //单选题
    private void fretchTreeByQuestion(RadioGroup group,
                                      InvestInfo wenJuanInfo, LinearLayout optionLl,
                                      String isAll) {

     LinearLayout ll=new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                70);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton rb=new RadioButton(getActivity());
        rb.setLayoutParams(lp);
        rb.setId(wenJuanInfo.getID());
        CurrCol.add(rb);
        group.addView(rb);


        //单选题选项的文字和输入框

        TextView tvLeft=new TextView(getActivity());
        tvLeft.setGravity(Gravity.CENTER);
        tvLeft.setLayoutParams(lp);
        tvLeft.setText(wenJuanInfo.getTITLE_L());
        ll.addView(tvLeft,lp);

        if(wenJuanInfo.isINPUT()){

            EditText et=new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0,-20,0,0);
            if(TextUtils.equals("数字",wenJuanInfo.getINPUT_TYPE())){
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            LinearLayout.LayoutParams etParams= new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
            ll.addView(et);

            if(!TextUtils.equals("",wenJuanInfo.getTITLE_R())){

                TextView tvRight=new TextView(getActivity());
                tvRight.setGravity(Gravity.CENTER);
                tvRight.setLayoutParams(lp);
                tvRight.setText(wenJuanInfo.getTITLE_R());
                ll.addView(tvRight,lp);

            }
        }


        optionLl.addView(ll,lp);
    }

    //数字题
    private void fretchTreeByQuestionShuzi(InvestInfo wenJuanInfo, LinearLayout optionLl,
                                      String isAll) {

        LinearLayout ll=new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvLeft=new TextView(getActivity());
        tvLeft.setText(wenJuanInfo.getTITLE_L());
        tvLeft.setLayoutParams(lp);
        ll.addView(tvLeft,lp);


        if(wenJuanInfo.isINPUT()){

            EditText et=new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0,-20,0,0);
            if(TextUtils.equals("数字",wenJuanInfo.getINPUT_TYPE())){
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            LinearLayout.LayoutParams etParams= new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
            ll.addView(et);

            if(!TextUtils.equals("",wenJuanInfo.getTITLE_R())){

                TextView tvRight=new TextView(getActivity());
                tvRight.setGravity(Gravity.CENTER);
                tvRight.setLayoutParams(lp);
                tvRight.setText(wenJuanInfo.getTITLE_R());
                ll.addView(tvRight,lp);

            }
        }

        optionLl.addView(ll,lp);
    }


    //第二级
    private void fretchTreeByQuestionTwo(InvestInfo wenJuanInfo, LinearLayout optionLl,
                                           String isAll) {


        LinearLayout ll=new LinearLayout(getActivity());//ll是第二级问题的布局
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvLeft=new TextView(getActivity());
        tvLeft.setText(wenJuanInfo.getTITLE_L());
        tvLeft.setLayoutParams(lp);
        ll.addView(tvLeft,lp);

        if(wenJuanInfo.isINPUT()){

            EditText et=new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0,-20,0,0);
            if(TextUtils.equals("数字",wenJuanInfo.getINPUT_TYPE())){
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            LinearLayout.LayoutParams etParams= new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
            ll.addView(et);

            if(!TextUtils.equals("",wenJuanInfo.getTITLE_R())){

                TextView tvRight=new TextView(getActivity());
                tvRight.setGravity(Gravity.CENTER);
                tvRight.setLayoutParams(lp);
                tvRight.setText(wenJuanInfo.getTITLE_R());
                ll.addView(tvRight,lp);

            }
        }

        optionLl.addView(ll,lp);
       //上面的代码是在弄二级问题的布局

        //下面的代码是在弄选项的布局
        LinearLayout aLl=new LinearLayout(getActivity());
        aLl.setOrientation(LinearLayout.HORIZONTAL);
        aLl.setLayoutParams(lp);

        RadioGroup radioGroup=new RadioGroup(getActivity());
        radioGroup.setLayoutParams(lp);


        LinearLayout oLl = new LinearLayout(getActivity());
        oLl.setLayoutParams(lp);
        oLl.setOrientation(LinearLayout.VERTICAL);

        answerThirdInfo = getThirdBySecondId(wenJuanInfo);//得到第三级选项的信息

        if(TextUtils.equals("单选",wenJuanInfo.getINPUT_TYPE())){//单选(还没完成)

            for (InvestInfo info : answerThirdInfo) {
                fretchTreeByQuestion(radioGroup, info,
                        oLl, isAll);
            }

        }else if(TextUtils.equals("数字",wenJuanInfo.getINPUT_TYPE())){
            for (InvestInfo info : answerThirdInfo) {
                fretchTreeByQuestionShuzi(info,
                        oLl, isAll);
            }
        }



        aLl.addView(radioGroup,lp);
        aLl.addView(oLl,lp);
        optionLl.addView(aLl,lp);


    }

    private  void showAlertDialog(final String mark){

        String title = null;

        if(TextUtils.equals(mark,"restart")){
            title="您确定要重新答题吗？";
        }

        final AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("温馨提示");
        builder.setMessage(title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(TextUtils.equals(mark,"restart")) {

                    showFirst();//默认显示第一题
                    btnLast.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);

                    btnAll.setVisibility(View.GONE);
                    btnRestart.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.GONE);

                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

         //获取单条数据
         private List<AnswerInfo> getAnswerInfo(int id,InvestInfo answer){

             List<AnswerInfo> list = new ArrayList<AnswerInfo>();

             AnswerInfo answerInfo = null;

             if(CurrCol.size()>0){

                 for(Object col:CurrCol){

                     if(col instanceof  EditText){

                         if(answerInfo==null){
                             answerInfo=new AnswerInfo();
                             answerInfo.setAnswerId(answer.getID());
                             answerInfo.setAnswerNo(answer.getID());//2018-01-16这里可能出错
                         }

                         EditText editText= (EditText) col;

                         // 如果是父标题的文本，加入集合
                         if(answer.getPARENT_ID()==0){

                             answerInfo.setAnswerText(editText.getText().toString().trim());
                             list.add(answerInfo);
                             continue;

                         }

                     }

                 }

             }

             return list;
         }

         private String parseAnswerInfo(List<AnswerInfo> answerInfos){

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

             return  array.toString();

         }

    //提交答案
    private void submitCom(){

        final HttpClient client = new DefaultHttpClient();

        final String strhttp = MyOkHttpUtils.BaseUrl+"/Json/Set_Qa_Receiv_Special.aspx?SQH="+personInfo.getSQH()+"&master_id=1";
        Log.e("2018-1-16","名字"+personInfo.getXM());
        Log.e("2018-1-16","企业提交url"+strhttp);
        new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        String cookies = SharedPreferencesUtils.getString("cookies");
                        HttpPost post = new HttpPost(strhttp);
                        try {
                            post.setHeader("cookie", cookies);
                            if (shujuliu!=null) {
                                Log.e("2018-1-16","企业提交shujuliu"+new String(shujuliu));
                                String str = Base64.encodeToString(shujuliu, Base64.DEFAULT);
                                StringEntity stringEntity = new StringEntity(str);
                                stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                                        "application/json"));
                                stringEntity.setContentEncoding(new BasicHeader(
                                        HTTP.CONTENT_ENCODING, HTTP.UTF_8));
                                post.setEntity(stringEntity);
                            }

                            HttpResponse response = client.execute(post);
                            Log.e("2018-1-16","企业提交响应码"+response.getStatusLine().getStatusCode());
                            if (response.getStatusLine().getStatusCode() == 200) {

                                HttpEntity entity=response.getEntity();
                                //EntityUtils中的toString()方法转换服务器的响应数据
                                final String str= EntityUtils.toString(entity, "utf-8");

                                Log.e("2018-1-16","企业提交str"+str);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(TextUtils.equals(str,"True")){
                                            Toast.makeText(getActivity(),"提交成功!",Toast.LENGTH_SHORT).show();


                                        }else{
                                            Toast.makeText(getActivity(),"提交失败!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });



                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        } finally {
                            post.abort();
                        }

                    }
                }

        ).start();

    }

}
