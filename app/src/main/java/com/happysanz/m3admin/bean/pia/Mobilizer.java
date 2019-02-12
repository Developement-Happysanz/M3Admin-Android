package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Mobilizer implements Serializable {

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("user_master_id")
    @Expose
    private String user_master_id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("user_name")
    @Expose
    private String user_name;

    @SerializedName("user_type_name")
    @Expose
    private String user_type_name;

    @SerializedName("status")
    @Expose
    private String status;

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
     * @return The user_master_id
     */
    public String getUser_master_id() {
        return user_master_id;
    }

    /**
     * @param user_master_id The user_master_id
     */
    public void setUser_master_id(String user_master_id) {
        this.user_master_id = user_master_id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The user_name
     */
    public String getUser_name() {
        return user_name;
    }

    /**
     * @param user_name The user_name
     */
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    /**
     * @return The user_type_name
     */
    public String getUser_type_name() {
        return user_type_name;
    }

    /**
     * @param user_type_name The user_type_name
     */
    public void setUser_type_name(String user_type_name) {
        this.user_type_name = user_type_name;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}