package com.youli.oldageassess.activity;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.youli.oldageassess.R;
import com.youli.oldageassess.adapter.CommonAdapter;
import com.youli.oldageassess.adapter.CommonViewHolder;
import com.youli.oldageassess.entity.AdminInfo;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.JdInfo;
import com.youli.oldageassess.entity.JwInfo;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.entity.ResultInfo;
import com.youli.oldageassess.utils.AlertDialogUtils;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.ProgressDialogUtils;
import com.youli.oldageassess.utils.TextViewUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Response;

/**
 * Created by liutao on 2018/1/12.
 * <p>
 * 待调查人员名单
 */

public class PersonListActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext = this;
    //private int lvClickNum;
    private final int SUCCEED = 10001;
    private final int SUCCEED_NODATA = 10002;
    private final int SUCCEED_INVEST = 10003;
    private final int PROBLEM = 10004;
    private final int OVERTIME = 10005;//登录超时

    private final int SUCCEED_JD = 10006;//街道
    private final int SUCCEED_JD_NODATA = 10007;
    private final int PROBLEM_JD = 10008;

    private final int SUCCEED_JW = 10009;//居委
    private final int SUCCEED_JW_NODATA = 10010;
    private final int PROBLEM_JW = 10011;
    private final int OVERTIME_JW = 10012;//居委超时

    private final int SUCCEED_RSEULT = 10013;//服务器记录的答案

    private int PageIndex = 0;

    private CommonAdapter jdAdapter, jwAdapter;

    private List<JdInfo> jdData = new ArrayList<>();

    private List<JwInfo> jwData = new ArrayList<>();

    private List<ResultInfo> resultData = new ArrayList<>();//服务器记录的答案

    private AlertDialogUtils adu;

    private ImageView ivBack;

    private ImageView ivSx;

    private int typeId;

    private AdminInfo adminInfo, adminInfo2;//操作员信息

    private PullToRefreshListView lv;
    private TextView tvNum, tvTitle;
    private List<PersonInfo> data = new ArrayList<>();
    private CommonAdapter adapter;

    private String startTime = "", endTime = "";//开始时间，结束时间
    private String jdId = "", jwId = "";//街道ID，居委ID

    private ProgressDialog progressDialog;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            dismissMyProgressDialog();

            switch (msg.what) {

                case SUCCEED:

                    if (PageIndex == 0) {
                        data.clear();
                    }
                    data.addAll((List<PersonInfo>) msg.obj);

                    if (data.size() > 0) {
                        tvNum.setVisibility(View.VISIBLE);
                        tvNum.setText("共有" + data.get(0).getRecordCount() + "人");
                    }
                    lv.setVisibility(View.VISIBLE);
                    LvSetAdapter(data);


                    break;
                case PROBLEM:

                    Toast.makeText(mContext, "网络不给力", Toast.LENGTH_SHORT).show();
                    if (lv.isRefreshing()) {
                        lv.onRefreshComplete();//停止刷新或加载更多
                    }

                    break;
                case SUCCEED_NODATA:
                    if (data.size() > 0) {
                        tvNum.setVisibility(View.VISIBLE);
                        tvNum.setText("共有" + data.get(0).getRecordCount() + "人");
                    }else {
                        tvNum.setVisibility(View.GONE);
                    }
                    Toast.makeText(mContext, "暂无数据", Toast.LENGTH_SHORT).show();


                    if (data.size()==0&&(TextUtils.equals("", startTime) || TextUtils.equals("", endTime) || TextUtils.equals("", jdId) || TextUtils.equals("", jwId))) {
                        lv.setVisibility(View.GONE);
                    }

                    if (lv.isRefreshing()) {
                        lv.onRefreshComplete();//停止刷新或加载更多
                    }
//                    if(data.size()==0) {
//                        //tvNoData.setVisibility(View.VISIBLE);
//                        lv.setVisibility(View.GONE);
//                    }
                    break;
                case OVERTIME:
                    ivSx.setEnabled(false);

                    Log.e("2018-1-26", "筛选超时");


//                    Intent i=new Intent(mContext,OvertimeDialogActivity.class);
//                    startActivity(i);
                    if (lv.isRefreshing()) {
                        lv.onRefreshComplete();//停止刷新或加载更多
                    }

                    break;

                case SUCCEED_INVEST:
                    investInfo = (ArrayList<InvestInfo>) (msg.obj);

                      getResultInfo(msg.arg1 - 1);

//                    Intent intent = new Intent(mContext, InvestActivity.class);
//                    intent.putExtra("pInfo", data.get(msg.arg1 - 1));
//                    intent.putExtra("type", typeId);
//                    intent.putExtra("adminInfo", adminInfo);
//                    intent.putExtra("adminInfo2", adminInfo2);
//                    startActivityForResult(intent, 100);


                    break;

                case SUCCEED_RSEULT:

                    resultData = (ArrayList<ResultInfo>) (msg.obj);

                    Intent intent = new Intent(mContext, InvestActivity.class);
                    intent.putExtra("pInfo", data.get(msg.arg1));
                    intent.putExtra("type", typeId);
                    intent.putExtra("adminInfo", adminInfo);
                    intent.putExtra("adminInfo2", adminInfo2);
                    intent.putExtra("resultData",(Serializable) resultData);
                    startActivityForResult(intent, 100);

                    break;

                case SUCCEED_JD:
                    ivSx.setEnabled(true);
                    jdData.clear();
                    jdData.add(new JdInfo("街  道"));
                    jdData.addAll((List<JdInfo>) msg.obj);
                    getJdData("JW");//获取街道信息和居委信息
                    break;

                case SUCCEED_JD_NODATA:
                    ivSx.setEnabled(false);
                    break;

                case PROBLEM_JD:
                    ivSx.setEnabled(false);
                    break;

                case SUCCEED_JW:
                    jwData.clear();
                    jwData.addAll((List<JwInfo>) msg.obj);
                    break;

                case SUCCEED_JW_NODATA:

                    break;

                case PROBLEM_JW:

                    break;
            }

        }
    };

    public static List<InvestInfo> investInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        typeId = getIntent().getIntExtra("type", 0);

        adminInfo = (AdminInfo) getIntent().getSerializableExtra("adminInfo");
        adminInfo2 = (AdminInfo) getIntent().getSerializableExtra("adminInfo2");
        initViews();
    }

    private void initViews() {

        ivSx = findViewById(R.id.iv_sx_person_list);
        ivSx.setOnClickListener(this);
        ivSx.setEnabled(false);
        ivBack = findViewById(R.id.iv_back_person_list);
        ivBack.setOnClickListener(this);

        tvNum = findViewById(R.id.tv_num_person_list);
        tvTitle = findViewById(R.id.tv_title_person_list);
        if (typeId == 1) {
            tvTitle.setText("待 调 查 人 员 名 单");
        } else if (typeId == 2) {
            tvTitle.setText("已 调 查 人 员 名 单");
        }

        lv = findViewById(R.id.lv_person_list);

        lv.setMode(PullToRefreshBase.Mode.BOTH);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                showMyProgressDialog(mContext);
                getInvestInfo(i);
            }
        });
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                //刷新
                PageIndex = 0;
                initDatas(PageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                //加载更多
                PageIndex++;
                initDatas(PageIndex);
            }
        });

        initDatas(0);

        getJdData("JD");//获取街道信息和居委信息

    }


    private void initDatas(final int pIndex) {


        new Thread(


                new Runnable() {
                    @Override
                    public void run() {

                        // http://web.youli.pw:81/Json/Get_SL.aspx?page=0&rows=20&type=1

                        String url = MyOkHttpUtils.BaseUrl + "/Json/Get_SL.aspx?page=" + pIndex + "&rows=15&type=" + typeId + "&ZZJD=" + jdId + "&ZZJW=" + jwId + "&SQDate1=" + startTime + "&SQDate2=" + endTime;

                        Log.e("2018-1-26", "筛选url=" + url);

                        Response response = MyOkHttpUtils.okHttpGet(url);

                        Message msg = Message.obtain();

                        if (response != null) {

                            try {
                                String meetStr = response.body().string();

                                if (!TextUtils.equals(meetStr, "[]") && !TextUtils.equals(meetStr, "[null]")) {

                                    Gson gson = new Gson();
                                    msg.obj = gson.fromJson(meetStr, new TypeToken<List<PersonInfo>>() {
                                    }.getType());
                                    msg.what = SUCCEED;
                                } else {
                                    msg.what = SUCCEED_NODATA;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                msg.what = OVERTIME;
                                Log.e("2018-1-26", "2222222222222222222222");
                            }

                        } else {

                            msg.what = PROBLEM;

                        }

                        mHandler.sendMessage(msg);

                    }
                }


        ).start();

    }

    private void LvSetAdapter(List<PersonInfo> list) {

        if (adapter == null) {

            adapter = new CommonAdapter<PersonInfo>(mContext, list, R.layout.item_person_list) {
                @Override
                public void convert(CommonViewHolder holder, PersonInfo item, int position) {

                    TextView tvName = holder.getView(R.id.tv_name_item_person_list);
                    tvName.setText(item.getXM());
                    ImageView ivSex = holder.getView(R.id.iv_sex_item_person_list);
                    if (TextUtils.equals(item.getXB(), "1")) {
                        ivSex.setImageResource(R.drawable.n);
                    } else if (TextUtils.equals(item.getXB(), "2")) {
                        ivSex.setImageResource(R.drawable.nv);
                    }
                    TextView tvAge = holder.getView(R.id.tv_age_item_person_list);
                    tvAge.setText(item.getAge() + "岁");

                    TextView tvDate = holder.getView(R.id.tv_date_item_person_list);
                    if (typeId == 1) {
                        SpannableString ss = new SpannableString("2018-01-12(未填)");
                        TextViewUtils.titleTvSetStyle("2018-01-12(未填)", ss, "(未填)");
                        tvDate.setText(ss);
                    } else {
                        tvDate.setText("2018-01-12");
                    }
                    TextView tvPhone = holder.getView(R.id.tv_phone_item_person_list);
                    tvPhone.setText(item.getSJHM());
                    TextView tvSfz = holder.getView(R.id.tv_sfz_item_person_list);
                    tvSfz.setText(item.getSFZH());
                    TextView tvAddress = holder.getView(R.id.tv_address_item_person_list);
                    tvAddress.setText(item.getZZL() + "路" + item.getZZN() + "弄" + item.getZZH() + "号" + item.getZZS() + "室");
                    TextView tvXzdc = holder.getView(R.id.tv_xzdc_item_person_list);
                    // tvXzdc.setText(item.getXzdc());


                }
            };
            lv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        if (lv.isRefreshing()) {
            lv.onRefreshComplete();//停止刷新或加载更多
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.iv_back_person_list:

                finish();

                break;

            case R.id.iv_sx_person_list:

                showShaiXuanDialog(jdData, jwData);//筛选对话框

                break;

        }

    }


    private void getInvestInfo(final int position) {

        new Thread(

                new Runnable() {
                    @Override
                    public void run() {

                        //http://web.youli.pw:81/Json/Get_Qa_Detil_Special.aspx

                        String url = MyOkHttpUtils.BaseUrl + "/Json/Get_Qa_Detil_Special.aspx";


                        Response response = MyOkHttpUtils.okHttpGet(url);

                        Message msg = Message.obtain();

                        if (response != null) {

                            try {
                                String meetStr = response.body().string();

                                if (!TextUtils.equals(meetStr, "[]") && !TextUtils.equals(meetStr, "[null]")) {

                                    Gson gson = new Gson();
                                    msg.obj = gson.fromJson(meetStr, new TypeToken<List<InvestInfo>>() {
                                    }.getType());
                                    msg.arg1 = position;
                                    msg.what = SUCCEED_INVEST;
                                } else {
                                    msg.what = SUCCEED_NODATA;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                msg.what = OVERTIME;
                                Log.e("2018-1-26", "333333333333333333333");
                            }

                        } else {

                            msg.what = PROBLEM;

                        }

                        mHandler.sendMessage(msg);

                    }
                }


        ).start();

    }


    private void showShaiXuanDialog(final List<JdInfo> jdList, final List<JwInfo> jwList) {

        //  jdId="";
        // jwId="";
        final Calendar c = Calendar.getInstance();
        adu = new AlertDialogUtils(this, R.layout.shaixuan_layout);
        adu.showAlertDialog();

        final List<JwInfo> myJwList = new ArrayList<>();
        final TextView startTimeTv = adu.getAduView().findViewById(R.id.shaixuan_layout_start_time_tv);//开始时间

        if (!TextUtils.equals("", startTime)) {
            startTimeTv.setText(startTime);
        }

        //startTime="";
        startTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startTimeTv.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        startTime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

                        if (year > c.get(Calendar.YEAR) || monthOfYear > c.get(Calendar.MONTH) || dayOfMonth > c.get(Calendar.DAY_OF_MONTH)) {
                            Toast.makeText(mContext, "时间必须是当年且小于当前时间!", Toast.LENGTH_SHORT).show();
                            startTimeTv.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                        }

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        final TextView endTimeTv = adu.getAduView().findViewById(R.id.shaixuan_layout_end_time_tv);//结束时间
        if (!TextUtils.equals(endTime, "")) {
            endTimeTv.setText(endTime);
        }
        //endTime="";
        endTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endTimeTv.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                        endTime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        if (year > c.get(Calendar.YEAR) || monthOfYear > c.get(Calendar.MONTH) || dayOfMonth > c.get(Calendar.DAY_OF_MONTH)) {
                            Toast.makeText(mContext, "时间必须是当年且小于当前时间!", Toast.LENGTH_SHORT).show();
                            endTimeTv.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
                        }

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Spinner spJd = adu.getAduView().findViewById(R.id.shaixuan_layout_jd_sp);


        final Spinner spJw = adu.getAduView().findViewById(R.id.shaixuan_layout_jw_sp);
        spJw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (myJwList != null) {
                    if (myJwList.get(i).getJWDM() != null) {
                        jwId = myJwList.get(i).getJWDM();
                    } else {
                        jwId = "";
                    }
                } else {
                    jwId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        jdAdapter = new CommonAdapter<JdInfo>(mContext, jdList, R.layout.sp_item) {
            @Override
            public void convert(CommonViewHolder holder, JdInfo item, int position) {

                TextView tv = holder.getView(R.id.tv_sp_item);

                tv.setText(TextViewUtils.appendOneSpace(item.getJDMC()));
            }
        };
        spJd.setAdapter(jdAdapter);

        if (!TextUtils.equals("", jdId) && jdData != null) {

            for (int i = 0; i < jdData.size(); i++) {

                if (TextUtils.equals(jdId, jdData.get(i).getJDDM())) {

                    spJd.setSelection(i);

                    break;
                }

            }

        }

        spJd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (jdList.get(i).getJDDM() != null) {
                    jdId = jdList.get(i).getJDDM();
                } else {
                    jdId = "";
                }
                myJwList.clear();
                myJwList.add(new JwInfo("居  委"));
                if (jwList.size() > 0) {

                    for (JwInfo jw : jwList) {

                        if (TextUtils.equals(jdList.get(i).getJDDM(), jw.getJWJD())) {

                            myJwList.add(jw);

                        }

                    }

                }

                jwAdapter = new CommonAdapter<JwInfo>(mContext, myJwList, R.layout.sp_item) {
                    @Override
                    public void convert(CommonViewHolder holder, JwInfo item, int position) {

                        TextView tv = holder.getView(R.id.tv_sp_item);

                        tv.setText(TextViewUtils.appendOneSpace(item.getJWMC()));
                    }
                };
                spJw.setAdapter(jwAdapter);

                if (!TextUtils.equals("", jwId) && myJwList != null) {

                    for (int j = 0; j < myJwList.size(); j++) {

                        if (TextUtils.equals(jwId, myJwList.get(j).getJWDM())) {

                            spJw.setSelection(j);

                            break;
                        }

                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button btnSure = adu.getAduView().findViewById(R.id.shaixuan_layout_sure_btn);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adu.dismissAlertDialog();

                PageIndex = 0;
                initDatas(PageIndex);
            }
        });

        Button btnCancel = adu.getAduView().findViewById(R.id.shaixuan_layout_cancel_btn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adu.dismissAlertDialog();

            }
        });
    }

    private void getJdData(final String mark) {

        new Thread(


                new Runnable() {
                    @Override
                    public void run() {

                        // http://183.194.4.58:81/Json/Get_JD.aspx
                        String url = null;
                        if (TextUtils.equals(mark, "JD")) {
                            url = MyOkHttpUtils.BaseUrl + "/Json/Get_JD.aspx";
                        } else if (TextUtils.equals(mark, "JW")) {
                            url = MyOkHttpUtils.BaseUrl + "/Json/Get_JW.aspx";
                        }

                        Response response = MyOkHttpUtils.okHttpGet(url);

                        Message msg = Message.obtain();

                        if (response != null) {

                            try {
                                String meetStr = response.body().string();

                                if (!TextUtils.equals(meetStr, "[]") && !TextUtils.equals(meetStr, "[null]")) {

                                    Gson gson = new Gson();
                                    if (TextUtils.equals(mark, "JD")) {
                                        msg.obj = gson.fromJson(meetStr, new TypeToken<List<JdInfo>>() {
                                        }.getType());
                                        msg.what = SUCCEED_JD;
                                    } else if (TextUtils.equals(mark, "JW")) {
                                        msg.obj = gson.fromJson(meetStr, new TypeToken<List<JwInfo>>() {
                                        }.getType());
                                        msg.what = SUCCEED_JW;
                                    }
                                } else {
                                    if (TextUtils.equals(mark, "JD")) {
                                        msg.what = SUCCEED_JD_NODATA;
                                    } else if (TextUtils.equals(mark, "JW")) {
                                        msg.what = SUCCEED_JW_NODATA;
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                if (TextUtils.equals(mark, "JD")) {
                                    Log.e("2018-1-26", "11111111111111111111");
                                    msg.what = OVERTIME;
                                } else if (TextUtils.equals(mark, "JW")) {
                                    msg.what = OVERTIME_JW;
                                }
                            }

                        } else {
                            if (TextUtils.equals(mark, "JD")) {
                                msg.what = PROBLEM_JD;
                            } else if (TextUtils.equals(mark, "JW")) {
                                msg.what = PROBLEM_JW;
                            }
                        }

                        mHandler.sendMessage(msg);

                    }
                }


        ).start();


    }


    private void getResultInfo(final int position) {

      showMyProgressDialog(mContext);

        new Thread(

                new Runnable() {
                    @Override
                    public void run() {

                        //http://183.194.4.58:81/Json/Get_Qa_Receiv_Special.aspx?SQH=0013CH101201801020001

                        String url = MyOkHttpUtils.BaseUrl + "/Json/Get_Qa_Receiv_Special.aspx?SQH="+data.get(position).getSQH();


                        Log.e("2018-2-8","答案url=="+url);

                        Response response = MyOkHttpUtils.okHttpGet(url);

                        Message msg = Message.obtain();

                        if (response != null) {

                            try {
                                String meetStr = response.body().string();

                                if (!TextUtils.equals(meetStr, "[]") && !TextUtils.equals(meetStr, "[null]")) {

                                    Gson gson = new Gson();
                                    msg.obj = gson.fromJson(meetStr, new TypeToken<List<ResultInfo>>() {
                                    }.getType());
                                }

                                msg.arg1 = position;
                                msg.what = SUCCEED_RSEULT;
                            } catch (Exception e) {
                                e.printStackTrace();
                                msg.what = OVERTIME;
                                Log.e("2018-1-26", "333333333333333333333");
                            }

                        } else {

                            msg.what = PROBLEM;

                        }

                        mHandler.sendMessage(msg);

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

    private void dismissMyProgressDialog(){

        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog=null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == 100) {

            PageIndex = 0;
            initDatas(PageIndex);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
