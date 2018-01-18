package com.youli.oldageassess.activity;


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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.youli.oldageassess.R;
import com.youli.oldageassess.adapter.CommonAdapter;
import com.youli.oldageassess.adapter.CommonViewHolder;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.PersonInfo;
import com.youli.oldageassess.utils.MyOkHttpUtils;
import com.youli.oldageassess.utils.ProgressDialogUtils;
import com.youli.oldageassess.utils.TextViewUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * Created by liutao on 2018/1/12.
 *
 * 待调查人员名单
 */

public class PersonListActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext=this;

    //private int lvClickNum;



    private final int SUCCEED=10001;
    private final int SUCCEED_NODATA=10002;
    private final int SUCCEED_INVEST=10003;
    private final int  PROBLEM=10004;
    private final int OVERTIME=10005;//登录超时

    private int PageIndex=0;

    private ImageView ivBack;

    private int typeId;

    private PullToRefreshListView lv;
    private TextView tvNum;
    private List<PersonInfo> data=new ArrayList<>();
    private CommonAdapter adapter;

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ProgressDialogUtils.dismissMyProgressDialog(mContext);
            switch (msg.what){

                case SUCCEED:

                    if(PageIndex==0) {
                        data.clear();
                    }
                    data.addAll((List<PersonInfo>)msg.obj);

                    if(data.size()>0){
                        tvNum.setVisibility(View.VISIBLE);
                        tvNum.setText("共有" + data.get(0).getRecordCount() + "人");
                    }

//                    if(data!=null) {
//                        if(sfzStr==null) {
//                            tvNum.setText("共有" + data.get(0).getRecordCount() + "人");
//                        }
//                        tvNoData.setVisibility(View.GONE);
//                        lv.setVisibility(View.VISIBLE);
//                    }
                    LvSetAdapter(data);


                    break;
                case PROBLEM:

                    Toast.makeText(mContext,"网络不给力",Toast.LENGTH_SHORT).show();
                    if(lv.isRefreshing()) {
                        lv.onRefreshComplete();//停止刷新或加载更多
                    }

                    break;
                case SUCCEED_NODATA:

                    tvNum.setVisibility(View.GONE);
                    Toast.makeText(mContext,"暂无数据",Toast.LENGTH_SHORT).show();
//                    if(data.size()==0) {
//                       //tvNoData.setVisibility(View.VISIBLE);
//                        lv.setVisibility(View.GONE);
//                    }
                    if(lv.isRefreshing()) {
                        lv.onRefreshComplete();//停止刷新或加载更多
                    }

                    break;
                case OVERTIME:

//                    Intent i=new Intent(mContext,OvertimeDialogActivity.class);
//                    startActivity(i);


                    break;

                case SUCCEED_INVEST:


                        Intent intent = new Intent(mContext, InvestActivity.class);
                        intent.putExtra("pInfo", data.get(msg.arg1 - 1));
                        intent.putExtra("investInfo", (ArrayList<InvestInfo>) (msg.obj));
                        startActivity(intent);



                    break;

            }

        }
    };




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        typeId=getIntent().getIntExtra("type",0);



        initViews();
    }

    private void initViews(){

        ivBack=findViewById(R.id.iv_back_person_list);
        ivBack.setOnClickListener(this);

        tvNum=findViewById(R.id.tv_num_person_list);

        lv=findViewById(R.id.lv_person_list);

        lv.setMode(PullToRefreshBase.Mode.BOTH);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                  ProgressDialogUtils.showMyProgressDialog(mContext);
                   getInvestInfo(i);


            }
        });
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                //刷新
                PageIndex=0;
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

    }


    private void initDatas(final int pIndex){


        new Thread(


                new Runnable() {
                    @Override
                    public void run() {

                       // http://web.youli.pw:81/Json/Get_SL.aspx?page=0&rows=20&type=1

                        String  url = MyOkHttpUtils.BaseUrl + "/Json/Get_SL.aspx?page="+pIndex+"&rows=15&type="+typeId;


                        Response response=MyOkHttpUtils.okHttpGet(url);

                        Message msg=Message.obtain();

                        if(response!=null){

                            try {
                                String meetStr=response.body().string();

                                if(!TextUtils.equals(meetStr,"[]")&&!TextUtils.equals(meetStr,"[null]")){

                                    Gson gson=new Gson();
                                    msg.obj=gson.fromJson(meetStr,new TypeToken<List<PersonInfo>>(){}.getType());
                                    msg.what=SUCCEED;
                                }else{
                                    msg.what=SUCCEED_NODATA;
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

    private void LvSetAdapter(List<PersonInfo> list){

        if(adapter==null) {

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
                    tvAge.setText("xx岁");
                    TextView tvDate = holder.getView(R.id.tv_date_item_person_list);
                    SpannableString ss=new SpannableString("2018-01-12(未填)");
                    TextViewUtils.titleTvSetStyle("2018-01-12(未填)",ss,"(未填)");
                    tvDate.setText(ss);
                    TextView tvPhone = holder.getView(R.id.tv_phone_item_person_list);
                    tvPhone.setText(item.getSJHM());
                    TextView tvSfz = holder.getView(R.id.tv_sfz_item_person_list);
                    tvSfz.setText(item.getSFZH());
                    TextView tvAddress = holder.getView(R.id.tv_address_item_person_list);
                    tvAddress.setText(item.getZZL()+item.getZZN()+item.getZZH()+item.getZZS());
                    TextView tvXzdc = holder.getView(R.id.tv_xzdc_item_person_list);
                   // tvXzdc.setText(item.getXzdc());



                }
            };
            lv.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }

        if(lv.isRefreshing()) {
            lv.onRefreshComplete();//停止刷新或加载更多
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.iv_back_person_list:

                finish();

                break;

        }

    }


    private void getInvestInfo(final int position){


        new Thread(


                new Runnable() {
                    @Override
                    public void run() {

                        //http://web.youli.pw:81/Json/Get_Qa_Detil_Special.aspx

                        String  url = MyOkHttpUtils.BaseUrl + "/Json/Get_Qa_Detil_Special.aspx";


                        Response response=MyOkHttpUtils.okHttpGet(url);

                        Message msg=Message.obtain();

                        if(response!=null){

                            try {
                                String meetStr=response.body().string();

                                if(!TextUtils.equals(meetStr,"[]")&&!TextUtils.equals(meetStr,"[null]")){

                                    Gson gson=new Gson();
                                    msg.obj=gson.fromJson(meetStr,new TypeToken<List<InvestInfo>>(){}.getType());
                                    msg.arg1=position;
                                    msg.what=SUCCEED_INVEST;
                                }else{
                                    msg.what=SUCCEED_NODATA;
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

}
