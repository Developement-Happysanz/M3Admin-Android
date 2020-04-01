package com.happysanz.m3admin.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.happysanz.m3admin.bean.pia.WorkMonth;
import com.happysanz.m3admin.fragments.DynamicWorkTypeFragment;

import java.util.ArrayList;

public class WorkTypeTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ArrayList<WorkMonth> WorkMonthArrayList;
    public WorkTypeTabAdapter(FragmentManager fm, int NumOfTabs, ArrayList<WorkMonth> categoryArrayList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.WorkMonthArrayList = categoryArrayList;
    }
    @Override
    public Fragment getItem(int position) {
        return DynamicWorkTypeFragment.newInstance(position,WorkMonthArrayList);
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}