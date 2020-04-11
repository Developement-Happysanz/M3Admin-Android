package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StartStopList {
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("start_stop_data")
    @Expose
    private ArrayList<StartStop> data = new ArrayList<StartStop>();

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
     * @return The StartStop
     */
    public ArrayList<StartStop> getStartStop() {
        return data;
    }

    /**
     * @param data The StartStop
     */
    public void setStartStop(ArrayList<StartStop> data) {
        this.data = data;
    }
}
