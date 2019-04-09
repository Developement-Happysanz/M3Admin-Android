package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProjectPeriodList {
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("PeriodList")
    @Expose
    private ArrayList<ProjectPeriod> data = new ArrayList<ProjectPeriod>();

    /**
     * @return The count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return The planData
     */
    public ArrayList<ProjectPeriod> getTaskData() {
        return data;
    }

    /**
     * @param data The planData
     */
    public void setTaskData(ArrayList<ProjectPeriod> data) {
        this.data = data;
    }
}