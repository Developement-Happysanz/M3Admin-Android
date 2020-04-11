package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StartStop  implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("tracking_status")
    @Expose
    private String tracking_status;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lng")
    @Expose
    private String lng;

    @SerializedName("start_stop_time")
    @Expose
    private String start_stop_time;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The tracking_status
     */
    public String gettracking_status() {
        return tracking_status;
    }

    /**
     * @param tracking_status The tracking_status
     */
    public void settracking_status(String tracking_status) {
        this.tracking_status = tracking_status;
    }

    /**
     * @return The address
     */
    public String getaddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setaddress(String address) {
        this.address = address;
    }

    /**
     * @return The lat
     */
    public String getlat() {
        return lat;
    }

    /**
     * @param lat The lat
     */
    public void setlat(String lat) {
        this.lat = lat;
    }

    /**
     * @return The user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id The user_id
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * @return The lng
     */
    public String getlng() {
        return lng;
    }

    /**
     * @param lng The lng
     */
    public void setlng(String lng) {
        this.lng = lng;
    }

    /**
     * @return The start_stop_time
     */
    public String getstart_stop_time() {
        return start_stop_time;
    }

    /**
     * @param start_stop_time The start_stop_time
     */
    public void setstart_stop_time(String start_stop_time) {
        this.start_stop_time = start_stop_time;
    }

}