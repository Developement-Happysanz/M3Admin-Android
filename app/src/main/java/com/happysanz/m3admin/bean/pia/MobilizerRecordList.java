package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MobilizerRecordList {
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("tracking_report")
    @Expose
    private ArrayList<MobilizerRecord> data = new ArrayList<MobilizerRecord>();

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
     * @return The MobilizerRecord
     */
    public ArrayList<MobilizerRecord> getMobilizerRecord() {
        return data;
    }

    /**
     * @param data The MobilizerRecord
     */
    public void setMobilizerRecord(ArrayList<MobilizerRecord> data) {
        this.data = data;
    }
}