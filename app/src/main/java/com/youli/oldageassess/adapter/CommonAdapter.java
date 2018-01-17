package com.youli.oldageassess.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by sfhan on 2017/11/7.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<T> mDatas;
    protected final int mItemLayoutId;

    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId){

        this.mContext=context;
        this.mDatas=mDatas;
        this.mItemLayoutId=itemLayoutId;

        if(context!=null){
            mInflater=LayoutInflater.from(context);
        }


    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final CommonViewHolder holder=getViewHolder(position,convertView,parent);

        convert(holder,getItem(position),position);

        return holder.getConvertView();
    }

    public abstract void convert(CommonViewHolder holder, T item, int position);

    private CommonViewHolder getViewHolder(int position,View convertView,ViewGroup parent){

        return CommonViewHolder.get(mContext,convertView,parent,mItemLayoutId,position);
    }

}

