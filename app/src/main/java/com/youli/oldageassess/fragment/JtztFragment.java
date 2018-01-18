package com.youli.oldageassess.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.List;
import java.util.regex.Pattern;

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

    private FragmentManager fm;

    private View view;

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

    // 一级的编辑框
    private List<EditText> questionEditTexts = new ArrayList<EditText>();

    private List<RadioButton> radioButtons = new ArrayList<RadioButton>();//二级单选

    // 二级的编辑框
    private List<EditText> editTexts = new ArrayList<EditText>();

    private int index = 0;

    private byte[] shujuliu;//答案数据流


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
                    fretchTree(index, llInvest, currentInfo, "");
                    Toast.makeText(getActivity(), "下一题的提交成功", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCEED_ALL://最后的提交

                    break;
                case PROBLEM://提交失败
                    btnNext.setEnabled(true);
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

        InvestInfo = (List<InvestInfo>) getArguments().getSerializable("investInfo");

        personInfo = (PersonInfo) getArguments().getSerializable("personInfo");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_jtzt, container, false);

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

        showFirst();//默认显示第一题
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_last://上一题

                btnAll.setVisibility(View.GONE);

                if (index == 0) {
                    Toast.makeText(getActivity(), "已经是第一题了", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (index == 1) {
                    btnLast.setVisibility(View.GONE);
                }
                index--;
                currentInfo = questionInfos.get(index);
                fretchTree(index, llInvest, currentInfo, "");
                break;

            case R.id.btn_next://下一题



                InvestInfo info = questionInfos.get(index);

                List<InvestInfo> tempSmallWenJuan = getAnswerByParentId(info);//选项

                int questionNo = info.getID();

                 String questionInutType = null;//一级输入类型
                 
                 for(InvestInfo infos:InvestInfo){
                     
                     if(questionNo==infos.getID()){
                         
                         questionInutType=infos.getINPUT_TYPE();
                         
                         break;
                     }
                     
                 }

                if (questionEditTexts.size() > 0) {//这个if里面是用来判断一级标题里面的填空是否做完

                    for (EditText editText : questionEditTexts) {

                        if (editText.getId() == questionNo && "".equals(editText.getText().toString().trim())) {
                            Toast.makeText(getActivity(), "答案不能为空!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                }
                             Log.e("2018-1-18","ID="+questionNo);
                if (tempSmallWenJuan.size() > 0 && !checkRadioIsChecked(answerInfo, questionNo)&&TextUtils.equals("单选",questionInutType)) {//这个if里面是用来判断二级单选是否做完
                    Toast.makeText(getActivity(), "答案不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (makeEdit(answerInfo)) {//这个if里面是用来判断二级单选的填空是否做完
                    return;
                }


                if (index == questionInfos.size() - 1) {
                    Toast.makeText(getActivity(), "已经是最后一题了", Toast.LENGTH_SHORT).show();
                    btnAll.setVisibility(View.VISIBLE);
                    return;
                }

                List<AnswerInfo> list=new ArrayList<>();

                if(!TextUtils.equals("无",info.getINPUT_TYPE())) {
                    ;
                    list  = getAnswerInfo(questionNo, info);

                }else{

                    for(InvestInfo infos:InvestInfo){

                        if(info.getID()==infos.getPARENT_ID()){

                            list.addAll(getAnswerInfo(questionNo, infos));

                        }

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
                btnNext.setEnabled(false);
                submitCom("下一题的提交");
//                btnNext.setEnabled(false);
//                btnLast.setVisibility(View.VISIBLE);
//                index++;
//                currentInfo=questionInfos.get(index);
//                fretchTree(index,llInvest, currentInfo, "");
                break;

            case R.id.btn_all://查看全部
                btnRestart.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);

                btnAll.setVisibility(View.GONE);
                btnLast.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);

                llInvest.removeAllViews();
//                for (InvestInfo answerInfo : questionInfos) {
//
//                    fretchTree(llInvest, answerInfo, "all");
//                }

                for (int i = 0; i < questionInfos.size(); i++) {
                    fretchTree(i, llInvest, questionInfos.get(i), "all");
                }

                break;

            case R.id.btn_restart://重新开始

                showAlertDialog("restart");

                break;

            case R.id.btn_submit://提交

                Toast.makeText(getActivity(), "提交", Toast.LENGTH_SHORT).show();
                fm.beginTransaction().hide(((InvestActivity) getActivity()).jtztF).show(((InvestActivity) getActivity()).jbxmF).commit();

                ((InvestActivity) getActivity()).rbThree.setChecked(true);

                break;
        }

    }

    //搭建布局
    private void fretchTree(int index, LinearLayout layout, InvestInfo info, String isAll) {

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

        LinearLayout qLl = new LinearLayout(getActivity());//问题的布局
        qLl.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvLeft = new TextView(getActivity());//问题左边的部分
        tvLeft.setText((index + 1) + "." + info.getTITLE_L());
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
            Log.e("2018-1-17", "111111111111111111111111111111111二级数据=" + info.getTITLE_L());
            if (TextUtils.equals("多选", info.getINPUT_TYPE())) {//多选

                //多选题
                List<CheckBox> CheckBoxGroup = new ArrayList<CheckBox>();

                for (InvestInfo wenJuanInfo : answerInfo) {
                    fretchTreeByQuestionMultiSelect(CheckBoxGroup, radioGroup,
                            wenJuanInfo, optionLl, isAll);
                }

            } else if (TextUtils.equals("单选", info.getINPUT_TYPE())) {//单选(还没完成)

                for (InvestInfo wenJuanInfo : answerInfo) {
                    fretchTreeByQuestion(radioGroup, wenJuanInfo,
                            optionLl, isAll);
                }

            } else if (TextUtils.equals("数字", info.getINPUT_TYPE()) || TextUtils.equals("无", info.getINPUT_TYPE())) {
                for (InvestInfo wenJuanInfo : answerInfo) {
                    fretchTreeByQuestionShuzi(wenJuanInfo,
                            optionLl, isAll);
                }
            }

        } else if (isThirdList(info) == 3) {//有第三级数据的
            Log.e("2018-1-17", "222222222222222222222222222222222222二级数据=" + info.getTITLE_L());

                for (InvestInfo wenJuanInfo : answerInfo) {

                fretchTreeByQuestionTwo(radioGroup, info, wenJuanInfo, optionLl, isAll);
            }

        }

        aLl.addView(radioGroup, allparam);
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
    private void fretchTreeByQuestionMultiSelect(List<CheckBox> cbGroup, RadioGroup group, InvestInfo wenJuanInfo,
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


        //多选题选项的文字和输入框
        TextView tvLeft = new TextView(getActivity());
        tvLeft.setGravity(Gravity.CENTER);
        tvLeft.setLayoutParams(lp);
        tvLeft.setText(wenJuanInfo.getTITLE_L());

        ll.addView(tvLeft, lp);

        if (wenJuanInfo.isINPUT()) {

            EditText et = new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0, -20, 0, 0);
            if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE())) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);

            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
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
    private void fretchTreeByQuestion(RadioGroup group,
                                      InvestInfo wenJuanInfo, LinearLayout optionLl,
                                      String isAll) {

        LinearLayout ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                70);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton rb = new RadioButton(getActivity());
        rb.setLayoutParams(lp);
        rb.setId(wenJuanInfo.getID());
        CurrCol.add(rb);
        group.addView(rb);

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

        //单选题选项的文字和输入框

        TextView tvLeft = new TextView(getActivity());
        tvLeft.setGravity(Gravity.CENTER);
        tvLeft.setLayoutParams(lp);
        tvLeft.setText(wenJuanInfo.getTITLE_L());
        ll.addView(tvLeft, lp);

        if (wenJuanInfo.isINPUT()) {

            EditText et = new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0, -20, 0, 0);
            if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE())) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

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

            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
            editTexts.add(et);
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
    private void fretchTreeByQuestionShuzi(InvestInfo wenJuanInfo, LinearLayout optionLl,
                                           String isAll) {

        LinearLayout ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvLeft = new TextView(getActivity());
        tvLeft.setText(wenJuanInfo.getTITLE_L());
        tvLeft.setLayoutParams(lp);
        ll.addView(tvLeft, lp);


        if (wenJuanInfo.isINPUT()) {

            EditText et = new EditText(getActivity());
            et.setId(wenJuanInfo.getID());
            et.setPadding(0, -20, 0, 0);
            if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE())) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
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
    private void fretchTreeByQuestionTwo(RadioGroup rGroup, InvestInfo pInfo, InvestInfo wenJuanInfo, LinearLayout optionLl,
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

        }

        TextView tvLeft = new TextView(getActivity());
        tvLeft.setText(wenJuanInfo.getTITLE_L());
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
            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(wenJuanInfo.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);

            CurrCol.add(et);
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
                fretchTreeByQuestion(radioGroup, info,
                        oLl, isAll);
            }

        } else if (TextUtils.equals("数字", wenJuanInfo.getINPUT_TYPE())) {
            for (InvestInfo info : answerThirdInfo) {
                fretchTreeByQuestionShuzi(info,
                        oLl, isAll);
            }
        } else if (TextUtils.equals("多选", wenJuanInfo.getINPUT_TYPE())) {
            List<CheckBox> CheckBoxGroup = new ArrayList<CheckBox>();

            for (InvestInfo info : answerThirdInfo) {
                fretchTreeByQuestionMultiSelect(CheckBoxGroup, radioGroup,
                        info, oLl, isAll);
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
        if (CurrCol.size() > 0) {

            for (Object col : CurrCol) {

                if (col instanceof RadioButton) {

                    RadioButton radioButton = (RadioButton) col;

                    if (radioButton.isChecked()) {
                        answerInfo = new AnswerInfo();
                        rb_selects.add(radioButton.getId());
                        answerInfo.setAnswerId(radioButton.getId());
                        answerInfo.setAnswerNo(answer.getID());//2018-01-17这里可能出错
                        answerInfo.setAnswerText("");
                        list.add(answerInfo);
                    }

                }else if(col instanceof EditText){

                    EditText editText= (EditText) col;

                    if(answer.getPARENT_ID()!=0&&editText.getId()==answer.getID()){
                        answerInfo=new AnswerInfo();
                        answerInfo.setAnswerId(answer.getID());
                        answerInfo.setAnswerNo(answer.getID());//2018-01-16这里可能出错
                        answerInfo.setAnswerText(editText.getText().toString().trim());
                        list.add(answerInfo);
                    }

                    if(answerInfo==null&&answer.getPARENT_ID()==0){
                             answerInfo=new AnswerInfo();
                             answerInfo.setAnswerId(answer.getID());
                             answerInfo.setAnswerNo(answer.getID());//2018-01-16这里可能出错


                         }

                         // 如果是父标题的文本，加入集合
                         if(answer.getPARENT_ID()==0){

                             answerInfo.setAnswerText(editText.getText().toString().trim());
                             list.add(answerInfo);

                             continue;

                         }

                    for (int i = 0; i <rb_selects.size(); i++) {


                        if (rb_selects.get(i) == editText.getId()) {

                            answerInfo.setAnswerText(editText.getText()
                                    .toString().trim());
                           continue;
                        }

                    }

                }

            }

        }

        if(list.size()==2&&list.get(0)==list.get(1)){

            list.remove(1);

            for(InvestInfo  invest:InvestInfo){

                if(list.get(0).getAnswerId()==invest.getID()){

                    if(!invest.isINPUT()){
                        list.get(0).setAnswerText("");
                    }

                }

            }

            return list;

        }

//        if(index>=0){
//
//            Log.e("2018-1-16", "结尾==" + list.get(index));
//       }
        Log.e("2018-1-16","结束=="+list);

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

    //判断是否有第三级数据
    private int isThirdList(InvestInfo info) {

        int levelNum = 2;

        List<InvestInfo> twoList = new ArrayList<>();

        for (InvestInfo bigInfo : InvestInfo) {

            if (info.getID() == bigInfo.getPARENT_ID()) {

                //Log.e("2018-1-17","第二级内容=="+bigInfo.getTITLE_L());

                twoList.add(bigInfo);

            }

        }

        for (InvestInfo bigInfo : InvestInfo) {

            for (InvestInfo twoInfo : twoList) {

                if (twoInfo.getID() == bigInfo.getPARENT_ID()) {

                    //Log.e("2018-1-17", "第三级内容==" + bigInfo.getTITLE_L());

                    levelNum = 3;

                    return levelNum;

                }

            }
        }


        return levelNum;

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
}
