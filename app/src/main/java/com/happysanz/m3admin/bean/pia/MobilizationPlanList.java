package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MobilizationPlanList {
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("planDetails")
    @Expose
    private ArrayList<MobilizationPlan> data = new ArrayList<MobilizationPlan>();

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
    public ArrayList<MobilizationPlan> getTaskData() {
        return data;
    }

    /**
     * @param data The planData
     */
    public void setTaskData(ArrayList<MobilizationPlan> data) {
        this.data = data;
    }
}