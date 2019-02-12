package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MobilizerList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("userList")
    @Expose
    private ArrayList<Mobilizer> mobilizerArrayList = new ArrayList<Mobilizer>();

    /**
     *
     * @return
     *     The count
     */
    public int getCount() {
        return count;
    }

    /**
     *
     * @param count
     *     The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     *
     * @return
     *     The mobilizerArrayList
     */
    public ArrayList<Mobilizer> getMobilizerArrayList() {
        return mobilizerArrayList;
    }

    /**
     *
     * @param mobilizerArrayList
     *     The mobilizerArrayList
     */
    public void setMobilizerArrayList(ArrayList<Mobilizer> mobilizerArrayList) {
        this.mobilizerArrayList = mobilizerArrayList;
    }
}
