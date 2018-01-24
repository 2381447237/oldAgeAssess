package com.youli.oldageassess.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.youli.oldageassess.R;
import com.youli.oldageassess.activity.InvestActivity;
import com.youli.oldageassess.activity.InvestJbzd;
import com.youli.oldageassess.entity.AdminInfo;
import com.youli.oldageassess.entity.AnswerInfo;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.ProgressDialogUtils;
import com.youli.oldageassess.utils.SharedPreferencesUtils;
import com.youli.oldageassess.utils.TextViewUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.Response;

/**
 * Created by liutao on 2018/1/13.
 * <p>
 * 家庭状态
 */

public class JtztFragment extends MyBaseFragment implements View.OnClickListener {

    private final int SUCCEED_LAST = 10000;//上一题的提交
    private final int SUCCEED_NEXT = 10001;//下一题的提交
    private final int SUCCEED_ALL = 10002;//最后的提交
    private final int PROBLEM = 10003;//提交失败
    private final int OVERTIME=10005;//登录超时
    private FragmentManager fm;

    private View view;

    private ProgressDialog pd;

    private Button btnLast, btnNext, btnAll, btnRestart, btnSubmit;

    private LinearLayout llInvest;//所有问卷的信息的布局

    private PersonInfo personInfo;//个人信息

    private List<InvestInfo> InvestInfo;//所有问卷的信息

    private InvestInfo currentInfo;// 用于表示当前题
    // 当前控件
    private List<Object> CurrCol = new ArrayList<Object>();

    private List<InvestInfo> questionInfos;//问题的集合，没有选项

    private List<InvestInfo> answerInfo = new ArrayList<InvestInfo>();//二级选项的集合

    private List<InvestInfo> answerThirdInfo = new ArrayList<InvestInfo>();//三级选项的集合

    private List<EditText> questionEditTexts = new ArrayList<EditText>();// 一级的编辑框

    private List<RadioButton> radioButtons = new ArrayList<RadioButton>();//二级单选

    private List<CheckBox> this_CheckBoxs = new ArrayList<CheckBox>();//二级多选

    private List<EditText> editTexts = new ArrayList<EditText>();// 二级的编辑框

    private List<RadioButton> radioThirdButtons = new ArrayList<RadioButton>();//三级单选

    private List<EditText> editThirdTexts = new ArrayList<EditText>();// 二级的编辑框

    private List<CheckBox> CheckThirdBoxs = new ArrayList<CheckBox>();//三级多选

    private List<Button> buttonList=new ArrayList<>();//疾病诊断的按钮

    private int index = 0;

    private int myLevelNum;//题目最多等级

    private byte[] shujuliu;//答案数据流

    private int typeId;//他等于1时是未答，他等于2时是已答

    private boolean isZbzd=false;//判断是否是疾病诊断

    private Handler handler=new Handler();

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case SUCCEED_LAST://上一题的提交


                    break;
                case SUCCEED_NEXT://下一题的提交
                    btnNext.setEnabled(true);
                    btnLast.setVisibility(View.VISIBLE);
                    index++;
                    currentInfo = questionInfos.get(index);
                    checkRb(currentInfo.getTYPE_ID());
                    fretchTree(index, llInvest, currentInfo, "");
                 //   Toast.makeText(getActivity(), "下一题的提交成功", Toast.LENGTH_SHORT).show();

                    Log.e("2018-1-22","下一题的提交成功");
                    break;
                case SUCCEED_ALL://最后的提交
                    (getActivity()).finish();

                    if(getActivity()!=null){
                        Toast.makeText(getActivity(),"提交完成",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PROBLEM://提交失败

                    btnNext.setEnabled(true);
//                if(myLevelNum==3) {
//                    btnLast.setVisibility(View.VISIBLE);
//                    index++;
//                    currentInfo = questionInfos.get(index);
//                    checkRb(currentInfo.getTYPE_ID());
//                    fretchTree(index, llInvest, currentInfo, "");
//                }
                    break;

            }
        }
    };

    public static final JtztFragment newInstance(List<InvestInfo> info, PersonInfo pInfo) {

        JtztFragment fragment = new JtztFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("investInfo", (Serializable) info);
        bundle.putSerializable("personInfo", pInfo);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getFragmentManager();

      //  InvestInfo = (List<InvestInfo>) getArguments().getSerializable("investInfo");

        personInfo = (PersonInfo) getArguments().getSerializable("personInfo");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_jtzt, container, false);

        typeId=((InvestActivity) getActivity()).typeId;

        InvestInfo=((InvestActivity)getActivity()).jtztList;
        loadData();
        return view;
    }


    @Override
    protected void loadData() {

        //Log.e("2018-1-13","============长度="+info.size());

        initViews();
    }

    private void initViews() {

        llInvest = view.findViewById(R.id.ll);
        btnLast = view.findViewById(R.id.btn_last);
        btnLast.setOnClickListener(this);
        btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        btnAll = view.findViewById(R.id.btn_all);
        btnAll.setOnClickListener(this);
        btnRestart = view.findViewById(R.id.btn_restart);
        btnRestart.setOnClickListener(this);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        Log.e("2018-1-22","FragmentTypeId=="+typeId);

     //   if(typeId==1) {
           // showFirst();//默认显示第一题

        showPageData();//展示部分


//        }else if(typeId==2){
//
//            btnRestart.setVisibility(View.VISIBLE);
//            btnSubmit.setVisibility(View.VISIBLE);
//
//            btnAll.setVisibility(View.GONE);
//            btnLast.setVisibility(View.GONE);
//            btnNext.setVisibility(View.GONE);
//
//            llInvest.removeAllViews();
//
//      ;
//            if (InvestInfo.size() > 0) {
//
//                questionInfos = getQuestionByParent();
//                if (questionInfos.size() > 0) {
//                    for (int i = 0; i < questionInfos.size(); i++) {
//                        fretchTree(i, llInvest, questionInfos.get(i), "all");
//                    }
//                }
//            }
    //    }
    }

    private void showFirst() {

        if (InvestInfo.size() > 0) {

            questionInfos = getQuestionByParent();
            if (questionInfos.size() > 0) {

                index = 0;
                fretchTree(index, llInvest, questionInfos.get(index), "");
            }
        }

    }

    private void showPageData(){

        btnRestart.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);
        btnAll.setVisibility(View.GONE);
        btnLast.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);

        llInvest.removeAllViews();
        questionInfos = getQuestionByParent();
        for (int i = 0; i < questionInfos.size(); i++) {
            fretchTree(i, llInvest, questionInfos.get(i), "all");

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_last://上一题

                isZbzd=false;
                btnAll.setVisibility(View.GONE);

                if (index == 0) {
                    Toast.makeText(getActivity(), "已经是第一题了", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (index == 1) {
                    btnLast.setVisibility(View.GONE);
                }

                // 去掉答题的文本
                for (EditText editText : questionEditTexts) {
                    if (currentInfo.getID() == editText.getId()) {
                        editText.setText("");
                    }
                }

                // 去掉小题的文本
                List<InvestInfo> currentList = getAnswerByParentId(currentInfo);
                for (InvestInfo wenJuanInfo : currentList) {
                    for (EditText editText : editTexts) {
                        if (editText.getId() == wenJuanInfo.getID()) {
                            editText.setText("");
                        }
                    }
                    for (EditText editText : editThirdTexts) {
                        if (editText.getId() == wenJuanInfo.getID()) {
                            editText.setText("");
                        }
                    }
                }


                // 去掉单选按钮
                for (InvestInfo wenJuanInfo : currentList) {
                    for (RadioButton radioButton : radioButtons) {
                        if (radioButton.getId() == wenJuanInfo.getID()) {
                            radioButton.setChecked(false);
                        }
                    }
                    for (RadioButton radioButton : radioThirdButtons) {
                        if (radioButton.getId() == wenJuanInfo.getID()) {
                            radioButton.setChecked(false);
                        }
                    }
                    // 去掉多选按钮
                    for (CheckBox checkBox : this_CheckBoxs) {
                        if (checkBox.getId() == wenJuanInfo.getID()) {
                            checkBox.setChecked(false);
                        }
                    }

                }


                for (InvestInfo info : InvestInfo) {//这个循环里面是去掉第3级里面的输入框和单选按钮

                    for (InvestInfo wenJuanInfo : currentList) {

                        if (info.getPARENT_ID() == wenJuanInfo.getID()) {

                            for (RadioButton radioButton : radioThirdButtons) {
                                if (radioButton.getId() == info.getID()) {
                                    radioButton.setChecked(false);
                                }
                            }

                            for (EditText editText : editThirdTexts) {
                                if (editText.getId() == info.getID()) {
                                    editText.setText("");
                                }
                            }

                            for (CheckBox checkBox : CheckThirdBoxs) {
                                if (checkBox.getId() == info.getID()) {
                                    checkBox.setChecked(false);
                                }
                            }
                        }
                    }
                }

                index--;
                currentInfo = questionInfos.get(index);

                checkRb(currentInfo.getTYPE_ID());

                fretchTree(index, llInvest, currentInfo, "");
                break;

            case R.id.btn_next://下一题

                if(isZbzd){

                    Toast.makeText(getActivity(), "已经是最后一题了", Toast.LENGTH_SHORT).show();
                    btnAll.setVisibility(View.VISIBLE);
                    return;

                }

                InvestInfo info = questionInfos.get(index);

                List<InvestInfo> tempSmallWenJuan = getAnswerByParentId(info);//选项

                int questionNo = info.getID();

                String questionInutType = null;//一级输入类型

                for (InvestInfo infos : InvestInfo) {

                    if (questionNo == infos.getID()) {

                        questionInutType = infos.getINPUT_TYPE();

                        break;
                    }

                }



                if (questionEditTexts.size() > 0) {//这个if里面是用来判断一级标题里面的填空是否做完

                    for (EditText editText : questionEditTexts) {

                        if(currentInfo==null){
                            if (editText.getId() == questionNo && "".equals(editText.getText().toString().trim())) {
                                Toast.makeText(getActivity(), "答案不能为空!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }else {

                            if (currentInfo.getTITLE_TOP() == null) {
                                if (editText.getId() == questionNo && "".equals(editText.getText().toString().trim())) {
                                    Toast.makeText(getActivity(), "答案不能为空!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }else {

                                if (!currentInfo.getTITLE_TOP().contains("补充事项")) {


                                    if (editText.getId() == questionNo && "".equals(editText.getText().toString().trim())) {
                                        Toast.makeText(getActivity(), "答案不能为空!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                        }
                       }
                  //  }
                }
                if (myLevelNum == 2 && tempSmallWenJuan.size() > 0 && !checkRadioIsChecked(answerInfo, questionNo) && (TextUtils.equals("单选", questionInutType))) {//这个if里面是用来判断二级单选是否做完
                    Toast.makeText(getActivity(), "答案不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (myLevelNum == 3 && tempSmallWenJuan.size() > 0 && !checkRadioIsChecked(answerInfo, questionNo) && (TextUtils.equals("单选", questionInutType))) {//这个if里面是用来判断二级单选是否做完
                    Toast.makeText(getActivity(), "答案不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (tempSmallWenJuan.size() > 0 && !checkRadioIsChecked(answerInfo, questionNo) && (TextUtils.equals("多选", questionInutType))) {//这个if里面是用来判断二级多选是否做完
                    Toast.makeText(getActivity(), "答案不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }



                if (myLevelNum == 2 && !info.getTITLE_L().contains("居住地址") && singleEdit(myLevelNum, info, answerInfo)) {//这个if里面是用来判断2级纯填空是否做完(这个方法虽然写的很难受，但是能用)
                    return;
                }

                  //2018-1-23 临时注释（注意这里啊）
                if (myLevelNum == 3 & tempSmallWenJuan.size() > 0 && !checkThirdRadioIsChecked(InvestInfo, CurrCol)) {//这个if里面是用来判断三级单选是否做完
                    Toast.makeText(getActivity(), "答案不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (myLevelNum == 3 && singleThirdEdit(myLevelNum, info, answerInfo)) {//这个if里面是用来对付17题的填空的
                    return;
                }

                if (makeEdit(answerInfo)) {//这个if里面是用来判断二级单选的填空是否做完
                    return;
                }

                if (makeThirdEdit(InvestInfo)) {//这个if里面是用来判断三级单选的填空是否做完
                    return;
                }


                if (makeEdit_checkBox(answerInfo))//这个if里面是用来判断二级多选的填空是否做完
                    return;

                if (index == questionInfos.size() - 1) {
                    Toast.makeText(getActivity(), "已经是最后一题了", Toast.LENGTH_SHORT).show();
                    btnAll.setVisibility(View.VISIBLE);
                    return;
                }

                List<AnswerInfo> list = new ArrayList<>();

                if (!TextUtils.equals("无", info.getINPUT_TYPE())) {
                    ;
                    Log.e("2018-1-19", "上上上上上上上上上上上上上上上上上上");

                    if(myLevelNum!=3) {

                        list = getAnswerInfo(questionNo, info);
                        Log.e("2018-1-22", "+++++++++++++++++++++++++++++++++++");
                    }else{

                        for (InvestInfo bigInfos : InvestInfo) {

                            list.addAll(getAnswerInfoThird(bigInfos, info));

                        }

                        list=getList(list);//这个是为了除去重复的元素的

                        List<AnswerInfo> answerInfoList=new ArrayList<>();
                        answerInfoList.addAll(list);

                        for(InvestInfo infos:InvestInfo){

                            if(TextUtils.equals("独居",infos.getTITLE_L())||TextUtils.equals("不需要",infos.getTITLE_L())){//这里是为了解决18题和19题的

                                if(answerInfoList.get(0).getAnswerId()==infos.getID()){

                                    list.clear();

                                    list.add(answerInfoList.get(0));
                                }

                            }

                        }


                    }
                } else {


                    if (myLevelNum == 2) {
                        Log.e("2018-1-19", "中中中中中中中中中中中中中中中中中中");
                        for (InvestInfo infos : InvestInfo) {

                            if (info.getID() == infos.getPARENT_ID()) {

                                list.addAll(getAnswerInfo(questionNo, infos));

                            }

                        }
                    } else if (myLevelNum == 3) {
                        Log.e("2018-1-19", "下下下下下下下下下下下下下下下下下下");

                        for (InvestInfo bigInfos : InvestInfo) {

                            list.addAll(getAnswerInfoThird(bigInfos, info));


                        }

                        list=getList(list);//这个是为了除去重复的元素的

                        for(RadioButton rb:radioThirdButtons){//这里还有bug 为了对付第17题

                            for(EditText et:editThirdTexts){

                                if(rb.getId()==et.getId()){

                                    if(list.size()>1) {

                                        if (list.get(0).getAnswerId() == list.get(1).getAnswerId()) {

                                            list.remove(0);

                                        }

                                        if (list.get(0).getAnswerId() == rb.getId() && !rb.isChecked()) {
                                            list.remove(0);
                                        }
                                    }
                                }

                            }

                        }

                    } else if (myLevelNum == 4) {

                    }
                }


                String answerString = parseAnswerInfo(list);

                Log.e("2018-1-16", "answerString=" + answerString);
                try {
                    shujuliu = answerString.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }



                //2018-1-17==提交答案


//                btnLast.setVisibility(View.VISIBLE);
//                index++;
//                currentInfo = questionInfos.get(index);
//                checkRb(currentInfo.getTYPE_ID());

                if(index==0){
                    currentInfo=null;
                }

                if(currentInfo!=null&&currentInfo.getTYPE_ID()==5){
                    llInvest.removeAllViews();
                    for(int i=0;i<InvestInfo.size();i++){

                        if(InvestInfo.get(i).getTYPE_ID()==5&&InvestInfo.get(i).getPARENT_ID()==0){

                            isZbzd=true;

                            fretchTreeJbzd(i,InvestInfo.get(i),llInvest,"");
                        }

                    }
                return;
                }
                    btnNext.setEnabled(false);
                    submitCom("下一题的提交");
                   // fretchTree(index, llInvest, currentInfo, "");

                break;

            case R.id.btn_all://查看全部

                btnRestart.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                btnAll.setVisibility(View.GONE);
                btnLast.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);

                llInvest.removeAllViews();

                                for (int i = 0; i < questionInfos.size(); i++) {
                                    fretchTree(i, llInvest, questionInfos.get(i), "all");

                                }

                break;

            case R.id.btn_restart://重新开始
                isZbzd=false;
                showAlertDialog("restart");

                break;

            case R.id.btn_submit://提交
                showAlertDialog("submit");
                break;
        }

    }

    //搭建布局
    private void fretchTree(int index, LinearLayout layout, InvestInfo info, String isAll) {

        if("".equals(isAll)&&info.getTYPE_ID()==5){
            llInvest.removeAllViews();
            for(int i=0;i<InvestInfo.size();i++){

                if(InvestInfo.get(i).getTYPE_ID()==5&&InvestInfo.get(i).getPARENT_ID()==0){

                    isZbzd=true;

                    fretchTreeJbzd(i,InvestInfo.get(i),llInvest,"");
                }

            }
            return;
        }

        if ("".equals(isAll)) {
            llInvest.removeAllViews();
        }

        CurrCol.clear();

        LinearLayout allLl = new LinearLayout(getActivity());//整体布局（包括问题和选项）
        allLl.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams allparam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        allLl.setLayoutParams(allparam);


        if (!TextUtils.equals("0", info.getTITLE_TOP()) && !TextUtils.equals(null, info.getTITLE_TOP()) && !TextUtils.equals("", info.getTITLE_TOP())) {
            TextView topTv = new TextView(getActivity());
            topTv.setText(info.getTITLE_TOP());
            topTv.setTextColor(Color.parseColor("#000000"));
            topTv.setTextSize(18);
            allLl.addView(topTv, allparam);
        }

        FlexboxLayout qLl = new FlexboxLayout(getActivity());//问题的布局
        qLl.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);
        //  qLl.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvLeft = new TextView(getActivity());//问题左边的部分
        if(info.getCODE()!=null) {
            tvLeft.setText(info.getCODE()+info.getTITLE_L());
        }else{
            tvLeft.setText(info.getTITLE_L());
        }
        tvLeft.setTextColor(Color.parseColor("#000000"));
        tvLeft.setTextSize(18);
        qLl.addView(tvLeft, allparam);

        if (info.isINPUT()) {

            EditText et = new EditText(getActivity());
            et.setId(info.getID());
            CurrCol.add(et);
            et.setPadding(0, -20, 0, 0);
            if (TextUtils.equals("数字", info.getINPUT_TYPE())) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(info.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            if(TextUtils.equals("姓名:",info.getTITLE_L())){
                et.setText(((InvestActivity)getActivity()).pInfo.getXM());
            }
            if(TextUtils.equals("年龄:",info.getTITLE_L())){
                et.setText(((InvestActivity)getActivity()).pInfo.getAge()+"");
            }
            if("all".equals(isAll)){
                et.setEnabled(false);
            }

            qLl.addView(et);

            if (questionEditTexts.size() > 0) {

                List<EditText> tempEditTexts = new ArrayList<>();
                for (EditText editText2 : questionEditTexts) {

                    if (editText2.getId() == info.getID()) {

                        et.setText(editText2.getText());

                        tempEditTexts.add(editText2);
                    }

                }
                questionEditTexts.removeAll(tempEditTexts);
                tempEditTexts.clear();
            }
            questionEditTexts.add(et);


            TextView tvRight = new TextView(getActivity());//问题右边的部分
            tvRight.setText(info.getTITLE_R());
            tvRight.setTextColor(Color.parseColor("#000000"));
            tvRight.setTextSize(18);
            qLl.addView(tvRight, allparam);
        }

        allLl.addView(qLl, allparam);
        //上面的代码是在弄问题的布局


        //下面的代码是在弄选项的布局
        LinearLayout aLl = new LinearLayout(getActivity());
        aLl.setOrientation(LinearLayout.HORIZONTAL);
        aLl.setLayoutParams(allparam);

        RadioGroup radioGroup = new RadioGroup(getActivity());
        radioGroup.setLayoutParams(allparam);

        LinearLayout optionLl = new LinearLayout(getActivity());
        optionLl.setLayoutParams(allparam);
        optionLl.setOrientation(LinearLayout.VERTICAL);

        answerInfo = getAnswerByParentId(info);//用问题的信息得到选项的信息

        if (isThirdList(info) == 2) {//没有第三级数据的
            Log.e("2018-1-21","2222222222222222222222222");
            if (TextUtils.equals("多选", info.getINPUT_TYPE())) {//多选
                //多选题
                List<CheckBox> CheckBoxGroup = new ArrayList<CheckBox>();
                for (InvestInfo wenJuanInfo : answerInfo) {
                    fretchTreeByQuestionMultiSelect(isThirdList(info), CheckBoxGroup, radioGroup,
                            wenJuanInfo, optionLl, isAll);
                }
            } else if (TextUtils.equals("单选", info.getINPUT_TYPE())) {//单选(还没完成)
                for (InvestInfo wenJuanInfo : answerInfo) {
                    fretchTreeByQuestion(isThirdList(info), radioGroup, wenJuanInfo,
                            optionLl, isAll);
                }
            } else if (TextUtils.equals("数字", info.getINPUT_TYPE()) || TextUtils.equals("无", info.getINPUT_TYPE())) {
                for (InvestInfo wenJuanInfo : answerInfo) {
                    fretchTreeByQuestionShuzi(isThirdList(info), wenJuanInfo, optionLl, isAll);
                }
            }
        } else if (isThirdList(info) == 3) {//有第三级数据的
            Log.e("2018-1-21","333333333333333333333333333333333");
            for (InvestInfo wenJuanInfo : answerInfo) {

                fretchTreeByQuestionTwo(isThirdList(info), radioGroup, info, wenJuanInfo, optionLl, isAll);
            }

        }else if(isThirdList(info) == 4){//有第四级数据的

            Log.e("2018-1-21","444444444444444444444444444");

//            for (InvestInfo wenJuanInfo : answerInfo) {
//
//                fretchTreeByQuestionFour(isThirdList(info), radioGroup, info, wenJuanInfo, optionLl, isAll);
//            }

        }

        LinearLayout.LayoutParams rgParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                80);


        aLl.addView(radioGroup,allparam);
        aLl.addView(optionLl, allparam);
        allLl.addView(aLl, allparam);

        layout.addView(allLl, allparam);
    }

    private List<InvestInfo> getQuestionByParent() {

        questionInfos = new ArrayList<InvestInfo>();
        for (InvestInfo info : InvestInfo) {
            if (info.getPARENT_ID() == 0) {
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
    private void fretchTreeByQuestionMultiSelect(int levelNum, List<CheckBox> cbGroup, RadioGroup group, InvestInfo wenJuanInfo,
                                                 LinearLayout optionLl, String isAll) {

        LinearLayout ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                70);

        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox cb = new CheckBox(getActivity());
        cb.setId(wenJuanInfo.getID());
        CurrCol.add(cb);
        cb.setLayoutParams(lp);
        group.addView(cb);

        if("all".equals(isAll)){
            cb.setEnabled(false);
        }
        if (levelNum == 2) {
        if (this_CheckBoxs.size() > 0) {
            List<CheckBox> tempRadioButtons = new ArrayList<>();
            for (CheckBox checkBox : this_CheckBoxs) {

                if (checkBox.getId() == wenJuanInfo.getID() && checkBox.isChecked()) {

                    cb.setChecked(true);
                    tempRadioButtons.add(checkBox);
                }
            }
            this_CheckBoxs.removeAll(tempRadioButtons);
            tempRadioButtons.clear();

        }

        for (int i = 0; i < this_CheckBoxs.size(); i++) {

            if (this_CheckBoxs.get(i).getId() == cb.getId()) {
                this_CheckBoxs.remove(i);
                break;
            }
        }

            this_CheckBoxs.add(cb);
        }else if(levelNum==3){

            if (CheckThirdBoxs.size() > 0) {
                List<CheckBox> tempRadioButtons = new ArrayList<>();
                for (CheckBox checkBox : CheckThirdBoxs) {

                    if (checkBox.getId() == wenJuanInfo.getID() && checkBox.isChecked()) {

                        cb.setChecked(true);
                        tempRadioButtons.add(checkBox);
                    }
                }
                CheckThirdBoxs.removeAll(tempRadioButtons);
                tempRadioButtons.clear();

            }

            for (int i = 0; i < CheckThirdBoxs.size(); i++) {

                if (CheckThirdBoxs.get(i).getId() == cb.getId()) {
                    CheckThirdBoxs.remove(i);
                    break;
                }
            }

            CheckThirdBoxs.add(cb);

        }
        //多选题选项的文字和输入框
        TextView tvLeft = new TextView(getActivity());
        tvLeft.setGravity(Gravity.CENTER);
        tvLeft.setLayoutParams(lp);
        if(wenJuanInfo.getCODE()!=null) {
            tvLeft.setText(wenJuanInfo.getCODE()+wenJuanInfo.getTITLE_L());
        }else{
            tvLeft.setText(wenJuanInfo.getTITLE_L());
        }
        ll.addView(tvLeft, lp);

        if (wenJuanInfo.isINPUT()) {

            EditText et = new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0, -20, 0, 0);
            if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE())) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if("all".equals(isAll)){
                et.setEnabled(false);
            }
            reDisplayEditText(levelNum, et, wenJuanInfo);

            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);

            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
            if (levelNum == 2) {
                editTexts.add(et);
            }
            ll.addView(et);

            if (!TextUtils.equals("", wenJuanInfo.getTITLE_R())) {

                TextView tvRight = new TextView(getActivity());
                tvRight.setGravity(Gravity.CENTER);
               tvRight.setLayoutParams(lp);
                tvRight.setText(wenJuanInfo.getTITLE_R());
                ll.addView(tvRight, lp);

            }
        }


        optionLl.addView(ll, lp);

    }


    //单选题
    private void fretchTreeByQuestion(int levelNum, RadioGroup group,
                                      InvestInfo wenJuanInfo, LinearLayout optionLl,
                                      String isAll) {

        LinearLayout ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                85);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton rb = new RadioButton(getActivity());
        rb.setLayoutParams(lp);
        rb.setId(wenJuanInfo.getID());
        CurrCol.add(rb);
        group.addView(rb);

        if(TextUtils.equals(((InvestActivity)getActivity()).pInfo.getXB(),"1")){//这里是用来填充性别的
            if(rb.getId()==25){
                rb.setChecked(true);
            }
        }else{
            if(rb.getId()==26){
                rb.setChecked(true);
            }
        }

        if(rb.getId()==32&&!TextUtils.equals(((InvestActivity)getActivity()).pInfo.getHJD_Name(),"")){//这里是用来填充是否为本地居民的
         rb.setChecked(true);
        }

        if(rb.getId()==33&&TextUtils.equals(((InvestActivity)getActivity()).pInfo.getHJD_Name(),"")){//这里是用来填充是否为本地居民的
            rb.setChecked(true);
        }

        if(rb.getId()==39&&TextUtils.equals(((InvestActivity)getActivity()).pInfo.getSFDB(),"0")){//这里是用来填充是否为低保的（第9题）
            rb.setChecked(true);
        }

        if("all".equals(isAll)){
            rb.setEnabled(false);
        }
        reDisplayRadioButton(levelNum, rb, wenJuanInfo);

        //单选题选项的文字和输入框

        TextView tvLeft = new TextView(getActivity());
        tvLeft.setGravity(Gravity.CENTER);
        tvLeft.setLayoutParams(lp);
        if(wenJuanInfo.getCODE()!=null) {
            tvLeft.setText(wenJuanInfo.getCODE()+wenJuanInfo.getTITLE_L());
        }else{
            tvLeft.setText(wenJuanInfo.getTITLE_L());
        }
        ll.addView(tvLeft, lp);

        if (wenJuanInfo.isINPUT()) {

            EditText et = new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0, -20, 0, 0);
            if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE())) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if("all".equals(isAll)){
                et.setEnabled(false);
            }
            reDisplayEditText(levelNum, et, wenJuanInfo);

            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
            if (levelNum == 2) {
                editTexts.add(et);
            }
            if (levelNum == 3) {
                editThirdTexts.add(et);
            }

            ll.addView(et);

            if (!TextUtils.equals("", wenJuanInfo.getTITLE_R())) {

                TextView tvRight = new TextView(getActivity());
                tvRight.setGravity(Gravity.CENTER);
                tvRight.setLayoutParams(lp);
                tvRight.setText(wenJuanInfo.getTITLE_R());
                ll.addView(tvRight, lp);

            }
        }


        optionLl.addView(ll, lp);
    }

    //数字题
    private void fretchTreeByQuestionShuzi(int levelNum, InvestInfo wenJuanInfo, LinearLayout optionLl,
                                           String isAll) {
        LinearLayout ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvLeft = new TextView(getActivity());
        if(wenJuanInfo.getCODE()!=null) {
            tvLeft.setText(wenJuanInfo.getCODE()+wenJuanInfo.getTITLE_L());
        }else{
            tvLeft.setText(wenJuanInfo.getTITLE_L());
        }
        tvLeft.setLayoutParams(lp);
        ll.addView(tvLeft, lp);


        if (wenJuanInfo.isINPUT()) {

            EditText et = new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0, -20, 0, 0);
            if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE())) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            reDisplayEditText(levelNum, et, wenJuanInfo);
            if("all".equals(isAll)){
                et.setEnabled(false);
            }
            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            if(TextUtils.equals(wenJuanInfo.getTITLE_R(),"区(县)")){
                et.setText(((InvestActivity)getActivity()).pInfo.getZZQX_Name());
            }

            if(TextUtils.equals(wenJuanInfo.getTITLE_R(),"小区街道(镇)")){
                et.setText(((InvestActivity)getActivity()).pInfo.getZJD_Name());
            }

            if(TextUtils.equals(wenJuanInfo.getTITLE_R(),"居(村)委")){
                et.setText(((InvestActivity)getActivity()).pInfo.getZJW_Name());
            }

            if(TextUtils.equals(wenJuanInfo.getTITLE_R(),"路")){
                et.setText(((InvestActivity)getActivity()).pInfo.getZZL());
            }

            if(TextUtils.equals(wenJuanInfo.getTITLE_R(),"弄")){
                et.setText(((InvestActivity)getActivity()).pInfo.getZZN());
            }

            if(TextUtils.equals(wenJuanInfo.getTITLE_R(),"号")){
                et.setText(((InvestActivity)getActivity()).pInfo.getZZH());
            }
            if(TextUtils.equals(wenJuanInfo.getTITLE_R(),"室")){
                et.setText(((InvestActivity)getActivity()).pInfo.getZZS());
            }

            CurrCol.add(et);
            if (levelNum == 2) {
                editTexts.add(et);
            } else if (levelNum == 3) {
                editThirdTexts.add(et);
            }
            ll.addView(et);


            if (!TextUtils.equals("", wenJuanInfo.getTITLE_R())) {

                TextView tvRight = new TextView(getActivity());
                tvRight.setGravity(Gravity.CENTER);
                tvRight.setLayoutParams(lp);
                tvRight.setText(wenJuanInfo.getTITLE_R());
                ll.addView(tvRight, lp);

            }
        }

        optionLl.addView(ll, lp);
    }


    //第二级
    private void fretchTreeByQuestionTwo(int levelNum, RadioGroup rGroup, InvestInfo pInfo, InvestInfo wenJuanInfo, LinearLayout optionLl,
                                         String isAll) {


        LinearLayout ll = new LinearLayout(getActivity());//ll是第二级问题的布局
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rglp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                70);
        if (TextUtils.equals(pInfo.getINPUT_TYPE(), "单选")) {

            RadioButton rb = new RadioButton(getActivity());
            rb.setLayoutParams(rglp);
            rb.setId(wenJuanInfo.getID());
            CurrCol.add(rb);
            rGroup.addView(rb, rglp);
            reDisplayRadioButton(levelNum, rb, wenJuanInfo);

            if("all".equals(isAll)){
                rb.setEnabled(false);
            }
        }




        TextView tvLeft = new TextView(getActivity());
        if(wenJuanInfo.getCODE()!=null) {
            tvLeft.setText(wenJuanInfo.getCODE()+wenJuanInfo.getTITLE_L());
        }else{
            tvLeft.setText(wenJuanInfo.getTITLE_L());
        }
        tvLeft.setPadding(0, 10, 0, 0);
        tvLeft.setLayoutParams(rglp);
        ll.addView(tvLeft, rglp);

        if (wenJuanInfo.isINPUT()) {

            EditText et = new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0, -20, 0, 0);
            if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE())) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if("all".equals(isAll)){
                et.setEnabled(false);
            }
            reDisplayEditText(levelNum, et, wenJuanInfo);

            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
            if (levelNum == 3) {
                editThirdTexts.add(et);
            }
            ll.addView(et);

            if (!TextUtils.equals("", wenJuanInfo.getTITLE_R())) {

                TextView tvRight = new TextView(getActivity());
                tvRight.setGravity(Gravity.CENTER);
                tvRight.setLayoutParams(lp);
                tvRight.setText(wenJuanInfo.getTITLE_R());
                ll.addView(tvRight, lp);

            }
        }

        optionLl.addView(ll, lp);
        //上面的代码是在弄二级问题的布局

        //下面的代码是在弄选项的布局
        LinearLayout aLl = new LinearLayout(getActivity());
        aLl.setOrientation(LinearLayout.HORIZONTAL);
        aLl.setLayoutParams(lp);

        RadioGroup radioGroup = new RadioGroup(getActivity());
        radioGroup.setLayoutParams(lp);


        LinearLayout oLl = new LinearLayout(getActivity());
        oLl.setLayoutParams(lp);
        oLl.setOrientation(LinearLayout.VERTICAL);

        answerThirdInfo = getThirdBySecondId(wenJuanInfo);//得到第三级选项的信息

        if (TextUtils.equals("单选", wenJuanInfo.getINPUT_TYPE())) {//单选(还没完成)

            for (InvestInfo info : answerThirdInfo) {


                fretchTreeByQuestion(levelNum, radioGroup, info,
                        oLl, isAll);
            }

        } else if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE()) || TextUtils.equals("无", wenJuanInfo.getINPUT_TYPE())) {
            for (InvestInfo info : answerThirdInfo) {
                fretchTreeByQuestionShuzi(levelNum, info,
                        oLl, isAll);
            }
        } else if (TextUtils.equals("多选", wenJuanInfo.getINPUT_TYPE())) {
            List<CheckBox> CheckBoxGroup = new ArrayList<CheckBox>();

            for (InvestInfo info : answerThirdInfo) {
                fretchTreeByQuestionMultiSelect(levelNum, CheckBoxGroup, radioGroup,
                        info, oLl, isAll);
            }
        }

        aLl.addView(radioGroup, lp);
        aLl.addView(oLl, lp);
        aLl.setPadding(30, 0, 0, 0);//第三级的数据往右移动30xp
        optionLl.addView(aLl, lp);


    }

    //第二级
    private void fretchTreeByQuestionFour(int levelNum, RadioGroup rGroup, InvestInfo pInfo, InvestInfo wenJuanInfo, LinearLayout optionLl,
                                         String isAll) {

        Log.e("2018-1-21","啦啦啦啦啦");

        LinearLayout ll = new LinearLayout(getActivity());//ll是第二级问题的布局
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rglp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                70);
        if (TextUtils.equals(pInfo.getINPUT_TYPE(), "单选")) {

            RadioButton rb = new RadioButton(getActivity());
            rb.setLayoutParams(rglp);
            rb.setId(wenJuanInfo.getID());
            CurrCol.add(rb);
            rGroup.addView(rb, rglp);
            reDisplayRadioButton(levelNum, rb, wenJuanInfo);

            if("all".equals(isAll)){
                rb.setEnabled(false);
            }
        }




        TextView tvLeft = new TextView(getActivity());
        if(wenJuanInfo.getCODE()!=null) {
            tvLeft.setText(wenJuanInfo.getCODE()+wenJuanInfo.getTITLE_L());
        }else{
            tvLeft.setText(wenJuanInfo.getTITLE_L());
        }
        tvLeft.setPadding(0, 10, 0, 0);
        tvLeft.setLayoutParams(rglp);
        ll.addView(tvLeft, rglp);

        if (wenJuanInfo.isINPUT()) {

            EditText et = new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0, -20, 0, 0);
            if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE())) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if("all".equals(isAll)){
                et.setEnabled(false);
            }
            reDisplayEditText(levelNum, et, wenJuanInfo);

            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
            if (levelNum == 3) {
                editThirdTexts.add(et);
            }
            ll.addView(et);

            if (!TextUtils.equals("", wenJuanInfo.getTITLE_R())) {

                TextView tvRight = new TextView(getActivity());
                tvRight.setGravity(Gravity.CENTER);
                tvRight.setLayoutParams(lp);
                tvRight.setText(wenJuanInfo.getTITLE_R());
                ll.addView(tvRight, lp);

            }
        }

        optionLl.addView(ll, lp);
        //上面的代码是在弄二级问题的布局

        //下面的代码是在弄选项的布局
        LinearLayout aLl = new LinearLayout(getActivity());
        aLl.setOrientation(LinearLayout.HORIZONTAL);
        aLl.setLayoutParams(lp);

        RadioGroup radioGroup = new RadioGroup(getActivity());
        radioGroup.setLayoutParams(lp);


        LinearLayout oLl = new LinearLayout(getActivity());
        oLl.setLayoutParams(lp);
        oLl.setOrientation(LinearLayout.VERTICAL);

        answerThirdInfo = getThirdBySecondId(wenJuanInfo);//得到第三级选项的信息

        if (TextUtils.equals("单选", wenJuanInfo.getINPUT_TYPE())) {//单选(还没完成)

            for (InvestInfo info : answerThirdInfo) {


                fretchTreeByQuestion(levelNum, radioGroup, info,
                        oLl, isAll);
            }

        }

        aLl.addView(radioGroup, lp);
        aLl.addView(oLl, lp);
        aLl.setPadding(30, 0, 0, 0);//第三级的数据往右移动30xp
        optionLl.addView(aLl, lp);


    }

    private void showAlertDialog(final String mark) {

        String title = null;

        if (TextUtils.equals(mark, "restart")) {
            title = "您确定要重新答题吗？";
        }else  if (TextUtils.equals(mark, "submit")) {
            title = "您确定要提交吗？";
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("温馨提示");
        builder.setMessage(title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.equals(mark, "restart")) {

                    showFirst();//默认显示第一题
                    btnLast.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);

                    btnAll.setVisibility(View.GONE);
                    btnRestart.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.GONE);

                }else {

                    submitAll();//最后的提交

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
    private List<AnswerInfo> getAnswerInfo(int id, InvestInfo answer) {

        List<AnswerInfo> list = new ArrayList<AnswerInfo>();

        AnswerInfo answerInfo = null;
        List<Integer> rb_selects = new ArrayList<Integer>();
        List<Integer> check_selects = new ArrayList<Integer>();
        if (CurrCol.size() > 0) {

            for (Object col : CurrCol) {

                if (col instanceof CheckBox) {

                    CheckBox cb = (CheckBox) col;

                    if (cb.isChecked()) {

                        answerInfo = new AnswerInfo();
                        check_selects.add(cb.getId());
                        answerInfo.setAnswerId(cb.getId());// answer.getID());
                        answerInfo.setAnswerNo(answer.getID());
                        answerInfo.setAnswerText("");
                        list.add(answerInfo);
                    }

                } else if (col instanceof RadioButton) {

                    RadioButton radioButton = (RadioButton) col;

                    if (radioButton.isChecked()) {
                        answerInfo = new AnswerInfo();
                        rb_selects.add(radioButton.getId());
                        answerInfo.setAnswerId(radioButton.getId());
                        answerInfo.setAnswerNo(answer.getID());//2018-01-17这里可能出错
                        answerInfo.setAnswerText("");
                        list.add(answerInfo);
                    }

                } else if (col instanceof EditText) {

                    EditText editText = (EditText) col;

                    if (answer.getPARENT_ID() != 0 && editText.getId() == answer.getID()) {
                        answerInfo = new AnswerInfo();
                        answerInfo.setAnswerId(answer.getID());
                        answerInfo.setAnswerNo(answer.getID());//2018-01-16这里可能出错
                        answerInfo.setAnswerText(editText.getText().toString().trim());
                        list.add(answerInfo);
                    }

                    if (answerInfo == null && answer.getPARENT_ID() == 0) {
                        answerInfo = new AnswerInfo();
                        answerInfo.setAnswerId(answer.getID());
                        answerInfo.setAnswerNo(answer.getID());//2018-01-16这里可能出错


                    }

                    // 如果是父标题的文本，加入集合
                    if (answer.getPARENT_ID() == 0) {

                        answerInfo.setAnswerText(editText.getText().toString().trim());
                        list.add(answerInfo);

                        continue;

                    }

                    for (int i = 0; i < rb_selects.size(); i++) {


                        if (rb_selects.get(i) == editText.getId()) {

                            answerInfo.setAnswerText(editText.getText()
                                    .toString().trim());
                            continue;
                        }

                    }
                    for (int i = 0; i < check_selects.size(); i++) {

                        if (check_selects.get(i) == editText.getId()) {

                            answerInfo.setAnswerText(editText.getText()
                                    .toString().trim());
                            continue;
                        }
                    }
                }
            }
        }
        if (!answer.isINPUT() && TextUtils.equals("单选", answer.getINPUT_TYPE())) {//这个if是为了解决第8题（单选题里面含有填空的）
            if (list.size() == 2 && list.get(0) == list.get(1)) {
                list.remove(1);
                for (InvestInfo invest : InvestInfo) {
                    if (list.get(0).getAnswerId() == invest.getID()) {
                        if (!invest.isINPUT()) {
                            list.get(0).setAnswerText("");
                        }
                    }
                }
                return list;
            }
        }

        if (answer.getTITLE_L().contains("常用语言") && TextUtils.equals("多选", answer.getINPUT_TYPE())) {//这个if是为了解决第16题（多选题里面含有填空的）
            for (InvestInfo infos : questionInfos) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getAnswerId() == infos.getID()) {
                        list.remove(i);
                    }
                }
            }
            if (list.size() == 3 && list.get(0) == list.get(1)) {
                list.remove(1);
            }
            if (list.size() == 4 && list.get(1) == list.get(2)) {
                list.remove(2);
            }
            if (list.size() == 3 && list.get(1) == list.get(2)) {
                list.remove(2);
            }
            for (InvestInfo infos : InvestInfo) {
                if (!infos.isINPUT()) {
                    if (list.get(0).getAnswerId() == infos.getID()) {
                        list.get(0).setAnswerText("");
                    }
                }
            }
            if (list.size() == 2 && list.get(0) == list.get(1)) {
                list.remove(1);
            }
        }

        return list;
    }

    private List<AnswerInfo> getAnswerInfoThird(InvestInfo bigInfo, InvestInfo twoInfo) {

        List<AnswerInfo> list = new ArrayList<AnswerInfo>();

        AnswerInfo answerInfo = null;

        if (CurrCol.size() > 0) {

            for (Object col : CurrCol) {

                if (col instanceof RadioButton) {

                    RadioButton rb = (RadioButton) col;

                    if (bigInfo.getPARENT_ID() == twoInfo.getID() && rb.isChecked()) {

                                answerInfo = new AnswerInfo();
                                answerInfo.setAnswerId(rb.getId());
                                answerInfo.setAnswerNo(rb.getId());
                                answerInfo.setAnswerText("");
                                list.add(answerInfo);

                    }

                }else if(col instanceof EditText) {

                    EditText et = (EditText) col;

                    for (RadioButton rb2 : radioThirdButtons) {

                        if (bigInfo.getPARENT_ID() == twoInfo.getID() && !"".equals(et.getText().toString().trim())) {
                            answerInfo = new AnswerInfo();
                            answerInfo.setAnswerId(et.getId());
                            answerInfo.setAnswerNo(et.getId());
                            answerInfo.setAnswerText(et.getText().toString().trim());
                            list.add(answerInfo);
                        }

                    }
                }else if(col instanceof  CheckBox) {

                        CheckBox cb = (CheckBox) col;

                            if (bigInfo.getPARENT_ID() == twoInfo.getID() && cb.isChecked()) {


                                answerInfo = new AnswerInfo();
                                answerInfo.setAnswerId(cb.getId());
                                answerInfo.setAnswerNo(cb.getId());
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

    //判断每题最多有第几级数据
    private int isThirdList(InvestInfo info) {

        myLevelNum = 2;
        List<InvestInfo> twoList = new ArrayList<>();
        List<InvestInfo> threeList = new ArrayList<>();
        for (InvestInfo bigInfo : InvestInfo) {
            if (info.getID() == bigInfo.getPARENT_ID()) {
                twoList.add(bigInfo);
            }
        }

        for (InvestInfo bigInfo : InvestInfo) {
            for (InvestInfo twoInfo : twoList) {
                if (twoInfo.getID() == bigInfo.getPARENT_ID()) {
                    myLevelNum = 3;
                    threeList.add(twoInfo);
                }
            }
        }

        for (InvestInfo bigInfo : twoList) {
            for (InvestInfo threeInfo : threeList) {
                if (threeInfo.getID() == bigInfo.getPARENT_ID()) {
                    myLevelNum = 4;
                    return myLevelNum;
                }

            }
        }


        return myLevelNum;

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

        for (CheckBox cb : this_CheckBoxs) {

            for (InvestInfo investInfo : infos) {

                if (cb.getId() == investInfo.getID() && investInfo.getPARENT_ID() == questionId
                        && cb.isChecked()) {

                    return true;

                }
            }
        }

        for (RadioButton radioButton : radioThirdButtons) {

            for (InvestInfo investInfo : infos) {

                if (radioButton.getId() == investInfo.getID() && investInfo.getPARENT_ID() == questionId
                        && radioButton.isChecked()) {

                    return true;

                }
            }
        }

        return false;
    }


    private boolean checkThirdRadioIsChecked(List<InvestInfo> infos, List<Object> CurrCol) {

Log.e("2018-1-22","checkThirdRadioIsChecked(List<InvestInfo> infos, List<Object> CurrCol)");
      //  for (InvestInfo info : infos) {

            for (RadioButton radioButton : radioThirdButtons) {

                for (InvestInfo investInfo : infos) {

                    if (radioButton.getId() == investInfo.getID() && radioButton.isChecked()) {

                        return true;

                    }
                }
            }

     //   }
        return false;
    }


    private boolean makeEdit(List<InvestInfo> infos) {

        if (editTexts.size() > 0) {

            for (RadioButton radioButton : radioButtons) {

                if (radioButton.isChecked()) {

                    for (EditText editText : editTexts) {

                        for (InvestInfo wenJuanInfo : infos) {

                            if (radioButton.getId() == wenJuanInfo.getID()
                                    && editText.getId() == wenJuanInfo.getID() && "".equals(editText.getText().toString().trim())) {

                                Toast.makeText(getActivity(), "答案不能为空!",
                                        Toast.LENGTH_SHORT).show();
                                return true;

                            }
                        }
                    }
                }
            }
        }
        return false;
    }




    private boolean makeThirdEdit(List<InvestInfo> infos) {

        if (editThirdTexts.size() > 0) {

            for (RadioButton radioButton : radioThirdButtons) {

                if (radioButton.isChecked()) {

                    for (EditText editText : editThirdTexts) {

                        for (InvestInfo wenJuanInfo : infos) {

                            if (radioButton.getId() == wenJuanInfo.getID()
                                    && editText.getId() == wenJuanInfo.getID() && "".equals(editText.getText().toString().trim())) {

                                Toast.makeText(getActivity(), "答案不能为空!",
                                        Toast.LENGTH_SHORT).show();
                                return true;

                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean singleEdit(int levelNum, InvestInfo infos, List<InvestInfo> aInfos) {

        //   if (levelNum == 2) {
        if (editTexts.size() > 0 && CurrCol.size() > 0) {
            for (Object col : CurrCol) {
                if (col instanceof EditText) {
                    for (InvestInfo aInfoList : aInfos) {
                        for (EditText et : editTexts) {
                            if (aInfoList.getID() == et.getId()) {
                                if (!TextUtils.equals("单选", infos.getINPUT_TYPE()) && !TextUtils.equals("多选", infos.getINPUT_TYPE()) && !infos.isINPUT()) {
                                    if ("".equals(et.getText().toString().trim())) {
                                        Toast.makeText(getActivity(), "答案不能为空!",
                                                Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //    }
        return false;
    }

    private boolean singleThirdEdit(int levelNum, InvestInfo infos, List<InvestInfo> aInfos) {

        //   if (levelNum == 2) {
        if (editThirdTexts.size() > 0 && CurrCol.size() > 0) {
            for (Object col : CurrCol) {
                if (col instanceof EditText) {
                    for (InvestInfo aInfoList : aInfos) {
                        for (EditText et : editThirdTexts) {
                            if (aInfoList.getID() == et.getId()) {
                                if (!TextUtils.equals("单选", infos.getINPUT_TYPE()) && !TextUtils.equals("多选", infos.getINPUT_TYPE()) && !infos.isINPUT()) {
                                    if ("".equals(et.getText().toString().trim())) {
                                        Toast.makeText(getActivity(), "答案不能为空!",
                                                Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //    }
        return false;
    }


    private boolean makeEdit_checkBox(List<InvestInfo> infos) {

        if (editTexts.size() > 0) {
            for (CheckBox radioButton : this_CheckBoxs) {
                if (radioButton.isChecked()) {
                    for (EditText editText : editTexts) {
                        for (InvestInfo wenJuanInfo : infos) {
                            if (radioButton.getId() == wenJuanInfo.getID() && editText.getId() == wenJuanInfo.getID() && "".equals(editText.getText().toString().trim())) {
                                Toast.makeText(getActivity(), "答案不能为空!",
                                        Toast.LENGTH_SHORT).show();

                                return true;
                            }

                        }
                    }
                }

            }
        }
        return false;

    }

    private void submitAnswer(final String type, final byte[] shujuliu, String sqh) {

        final HttpClient client = new DefaultHttpClient();

        final String strhttp = MyOkHttpUtils.BaseUrl + "/Json/Set_Qa_Receiv_Special.aspx?SQH=" + sqh + "&master_id=1";
        Log.e("2018-1-16", "企业提交url" + strhttp);
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
                                Log.e("2018-1-16", "企业提交shujuliu" + new String(shujuliu));
                                ByteArrayEntity arrayEntity = new ByteArrayEntity(shujuliu);
                                arrayEntity.setContentType("application/octet-stream");
                                post.setEntity(arrayEntity);
                            }

                            HttpResponse response = client.execute(post);
                            Log.e("2018-1-16", "企业提交响应码" + response.getStatusLine().getStatusCode());
                            if (response.getStatusLine().getStatusCode() == 200) {

                                HttpEntity entity = response.getEntity();
                                //EntityUtils中的toString()方法转换服务器的响应数据
                                final String str = EntityUtils.toString(entity, "utf-8");

                                Log.e("2018-1-16", "企业提交str" + str);

                                if (TextViewUtils.firstIsNumber(str)) {

                                    if (TextUtils.equals(type, "下一题的提交")) {
                                        msg.obj = str;
                                        msg.what = SUCCEED_NEXT;
                                    }
                                } else {

                                    msg.what = PROBLEM;
                                }

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

    //重新显示输入框里面的内容
    private void reDisplayEditText(int levelNum, EditText et, InvestInfo wenJuanInfo) {


        if (levelNum == 2) {

            if (editTexts.size() > 0) {
                List<EditText> tempEditTexts = new ArrayList<>();
                for (EditText editText2 : editTexts) {
                    if (editText2.getId() == wenJuanInfo.getID()) {
                        et.setText(editText2.getText());
                        tempEditTexts.add(editText2);
                    }
                }
                editTexts.removeAll(tempEditTexts);
                tempEditTexts.clear();
            }

        } else if (levelNum == 3) {
            if (editThirdTexts.size() > 0) {
                List<EditText> tempEditTexts = new ArrayList<>();
                for (EditText editText2 : editThirdTexts) {
                    if (editText2.getId() == wenJuanInfo.getID()) {
                        et.setText(editText2.getText());
                        tempEditTexts.add(editText2);
                    }
                }
                editThirdTexts.removeAll(tempEditTexts);
                tempEditTexts.clear();
            }

        }


    }

    //重新显示输入RadioButton的内容
    private void reDisplayRadioButton(int levelNum, RadioButton rb, InvestInfo wenJuanInfo) {


        if (levelNum == 2) {
            if (radioButtons.size() > 0) {
                List<RadioButton> tempRadioButtons = new ArrayList<>();
                for (RadioButton radioButton2 : radioButtons) {
                    if (radioButton2.getId() == wenJuanInfo.getID() && radioButton2.isChecked()) {
                        rb.setChecked(true);
                        tempRadioButtons.add(radioButton2);
                    }
                }
                radioButtons.removeAll(tempRadioButtons);
                tempRadioButtons.clear();
            }
            radioButtons.add(rb);
        } else if (levelNum == 3) {
            if (radioThirdButtons.size() > 0) {
                List<RadioButton> tempRadioButtons = new ArrayList<>();
                for (RadioButton radioButton2 : radioThirdButtons) {
                    if (radioButton2.getId() == wenJuanInfo.getID() && radioButton2.isChecked()) {
                        rb.setChecked(true);
                        tempRadioButtons.add(radioButton2);
                    }
                }
                radioThirdButtons.removeAll(tempRadioButtons);
                tempRadioButtons.clear();
            }
            radioThirdButtons.add(rb);
        }


    }

    private  ArrayList getList(List arr) {//集合去重

        List list = new ArrayList();

        Iterator it = arr.iterator();

        while (it.hasNext()) {

            Object obj = (Object) it.next();

            if(!list.contains(obj)){                //不包含就添加

                list.add(obj);
            }
        }
        return (ArrayList) list;
    }

    private void checkRb(int i){

        switch (i){

            case 2:

                ((InvestActivity)getActivity()).rbTwo.setChecked(true);

                break;

            case 3:

                ((InvestActivity)getActivity()).rbThree.setChecked(true);

                break;

            case 4:

                ((InvestActivity)getActivity()).rbFour.setChecked(true);

                break;

            case 5:

                ((InvestActivity)getActivity()).rbFive.setChecked(true);

                break;

        }

    };
    //第五部分
    private void fretchTreeJbzd(int i, final InvestInfo info, LinearLayout ll, String isAll){

        TextView tvTop=new TextView(getActivity());
        tvTop.setText(info.getTITLE_TOP());
        tvTop.setTextColor(Color.parseColor("#000000"));
        tvTop.setTextSize(18);
        ll.addView(tvTop);
        InvestActivity _a = (InvestActivity)getActivity();
        final Button btn=new Button(_a);
        _a.map.put(info.getTITLE_L(),btn);

        btn.setText(info.getTITLE_L());
        btn.setId(info.getID());
        buttonList.add(btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent((getActivity()),InvestJbzd.class);
             //   i.putExtra("info",(Serializable) InvestInfo);
                i.putExtra("id",info.getID());
                i.putExtra("title",btn.getText().toString());
                i.putExtra("personInfo",personInfo);
                startActivityForResult(i, 0);
            }
        });
        ll.addView(btn);
    };

    //获取单条数据
    private List<AnswerInfo> getButtonAnswerInfo(Button btn) {

        List<AnswerInfo> list = new ArrayList<AnswerInfo>();

        AnswerInfo answerInfo = null;


                        answerInfo = new AnswerInfo();
                        answerInfo.setAnswerId(btn.getId());
                        answerInfo.setAnswerNo(btn.getId());//2018-01-17这里可能出错
                        answerInfo.setAnswerText("");
                        list.add(answerInfo);




        return list;
    }

    private void submitAll(){

        new Thread(

                new Runnable() {
                    @Override
                    public void run() {

                        //http://web.youli.pw:81/Json/Get_Staff.aspx

                        String  url = MyOkHttpUtils.BaseUrl + "/Json/Set_Qa_Receiv_Special.aspx?SQH=" + personInfo.getSQH() + "&RECEIVED_STAFF1="+((InvestActivity)getActivity()).adminId+ "&RECEIVED_STAFF2="+((InvestActivity)getActivity()).adminId;

                        Log.e("2018-1-23","最后的url=="+url);
                        Response response=MyOkHttpUtils.okHttpGet(url);

                        Message msg=Message.obtain();

                        if(response!=null){

                            try {
                                String str=response.body().string();

                                Log.e("2018-1-23","最后的提交=="+str);

                                if(!TextUtils.equals(str,"")){

                                    msg.what=SUCCEED_ALL;

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                msg.what=OVERTIME;

                            }

                        }else{

                            msg.what=PROBLEM;

                        }

                        mHandler.sendMessage(msg);

                    }
                }


        ).start();


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
