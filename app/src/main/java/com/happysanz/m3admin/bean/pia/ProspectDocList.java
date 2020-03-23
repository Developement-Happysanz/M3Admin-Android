package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProspectDocList {
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("doc_data")
    @Expose
    private ArrayList<ProspectDoc> data = new ArrayList<ProspectDoc>();

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
    public ArrayList<ProspectDoc> getTaskData() {
        return data;
    }

    /**
     * @param data The planData
     */
    public void setTaskData(ArrayList<ProspectDoc> data) {
        this.data = data;
    }
}