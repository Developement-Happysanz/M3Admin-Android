package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SchemePhotoList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("scheme_gallery")
    @Expose
    private ArrayList<SchemePhoto> data = new ArrayList<SchemePhoto>();

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
     * @return The CenterPhotosData
     */
    public ArrayList<SchemePhoto> getCenterPhotosData() {
        return data;
    }

    /**
     * @param data The CenterPhotosData
     */
    public void setCenterPhotosData(ArrayList<SchemePhoto> data) {
        this.data = data;
    }
}
