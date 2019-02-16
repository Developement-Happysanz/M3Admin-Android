package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TaskDataList {
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("taskDetails")
    @Expose
    private ArrayList<TaskData> data = new ArrayList<TaskData>();

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
     * @return The TaskData
     */
    public ArrayList<TaskData> getTaskData() {
        return data;
    }

    /**
     * @param data The TaskData
     */
    public void setTaskData(ArrayList<TaskData> data) {
        this.data = data;
    }
}
