package com.youli.oldageassess.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by liutao on 2018/1/7.
 *
 * 禁止左右滑动的ViewPager
 */

public class MyViewPager extends ViewPager {


    private boolean enabled;


    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    //去除页面切换时的滑动翻页效果
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,false);
    }
}
