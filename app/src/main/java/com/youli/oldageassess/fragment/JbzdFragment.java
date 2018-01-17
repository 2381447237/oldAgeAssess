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
 * 疾病诊断
 */

public class JbzdFragment extends MyBaseFragment{

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_jbzd,container,false);



        return view;
    }


    @Override
    protected void loadData() {

    }
}
