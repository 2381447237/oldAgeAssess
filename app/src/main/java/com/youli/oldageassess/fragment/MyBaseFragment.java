package com.youli.oldageassess.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * 作者: zhengbin on 2017/11/26.
 * <p>
 * 邮箱:2381447237@qq.com
 * <p>
 * github:2381447237
 */

public abstract class MyBaseFragment extends Fragment {


    //Fragment的View加载完毕的标记
    private boolean isViewCreated;
    //Fragment对用户可见的标记
    private boolean isUIVisible;
    //判断是否是默认显示的fragment
    public boolean isFirst;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // Log.e("2017/12/1","================================");

        isViewCreated = true;

        lazyLoad();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!isFirst) {
            if (!hidden) {
                isUIVisible = true;
                lazyLoad();
            } else {
                isUIVisible = false;
            }
        }
    }


    private void lazyLoad() {
        //这里进行双重标记判断,是因为setUserVisibleHint会多次回调,并且会在onCreateView执行前回调,必须确保onCreateView加载完毕且页面可见,才加载数据
        if (isViewCreated && isUIVisible) {
            loadData();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            isUIVisible = false;

        }else {

            if (isFirst) {
                loadData();
            }
        }
    }


    protected abstract void loadData();


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //页面销毁,恢复标记
        isViewCreated = false;
        isUIVisible = false;
        isFirst=false;
    }


}
