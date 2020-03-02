package com.happysanz.m3admin.bean.tnsrlm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SchemeList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("schemeDetails")
    @Expose
    private ArrayList<Scheme> SchemeArrayList = new ArrayList<Scheme>();

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
     *     The SchemeArrayList
     */
    public ArrayList<Scheme> getSchemeArrayList() {
        return SchemeArrayList;
    }

    /**
     *
     * @param SchemeArrayList
     *     The SchemeArrayList
     */
    public void setSchemeArrayList(ArrayList<Scheme> SchemeArrayList) {
        this.SchemeArrayList = SchemeArrayList;
    }
}
