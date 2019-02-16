package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CentersList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("centerList")
    @Expose
    private ArrayList<Centers> centers = new ArrayList<Centers>();

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
     * @return The centers
     */
    public ArrayList<Centers> getCenters() {
        return centers;
    }

    /**
     * @param centers The centers
     */
    public void setCenters(ArrayList<Centers> centers) {
        this.centers = centers;
    }
}