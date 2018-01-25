package com.youli.oldageassess.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.youli.oldageassess.fragment.MyBaseFragment;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class InvestPageFragmentAdapter extends FragmentPagerAdapter {

    private final ArrayList<MyBaseFragment> list;

    public InvestPageFragmentAdapter(FragmentManager fm, ArrayList<MyBaseFragment> list) {
        super(fm);
        this.list = list;

    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }
}
