package com.youli.oldageassess.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youli.oldageassess.R;

/**
 * Created by liutao on 2018/1/13.
 *
 * 诚信声明
 */

public class CxsmFragment extends MyBaseFragment{

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_cxsm,container,false);

        isFirst=true;//如果是第一个fragment就给它赋值true，其他的fragment不用管这个变量

        return view;
    }



    @Override
    protected void loadData() {

    }
}
