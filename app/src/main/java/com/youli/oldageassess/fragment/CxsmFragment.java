package com.youli.oldageassess.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.youli.oldageassess.R;
import com.youli.oldageassess.activity.InvestActivity;
import com.youli.oldageassess.entity.InvestInfo;
import com.youli.oldageassess.entity.PersonInfo;

import java.util.List;

/**
 * Created by liutao on 2018/1/13.
 *
 * 诚信声明
 */

public class CxsmFragment extends MyBaseFragment{

    private View view;

    public Button btnStart;

    private FragmentManager fm;

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

        return view;
    }



    @Override
    protected void loadData() {

        btnStart=view.findViewById(R.id.btn_start_fragment_cxsm);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((InvestActivity) getActivity()).vp.setCurrentItem(1);

                ((InvestActivity) getActivity()).rbTwo.setChecked(true);

                btnStart.setVisibility(View.GONE);

            }
        });

    }
}
