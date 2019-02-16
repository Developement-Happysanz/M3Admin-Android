package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Centers implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("center_name")
    @Expose
    private String center_name;

    @SerializedName("center_info")
    @Expose
    private String center_info;

    @SerializedName("center_banner")
    @Expose
    private String center_banner;

    @SerializedName("center_address")
    @Expose
    private String center_address;

    @SerializedName("pia_Name")
    @Expose
    private String pia_Name;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("created_by")
    @Expose
    private String created_by;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_by")
    @Expose
    private String updated_by;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    /**
     * @return The id
     */
    public String getid() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setid(String id) {
        this.id = id;
    }

    /**
     * @return The center_name
     */
    public String getCenter_name() {
        return center_name;
    }

    /**
     * @param center_name The center_name
     */
    public void setCenter_name(String center_name) {
        this.center_name = center_name;
    }

    /**
     * @return The center_info
     */
    public String getcenter_info() {
        return center_info;
    }

    /**
     * @param center_info The center_info
     */
    public void setcenter_info(String center_info) {
        this.center_info = center_info;
    }

    /**
     * @return The center_banner
     */
    public String getcenter_banner() {
        return center_banner;
    }

    /**
     * @param center_banner The center_banner
     */
    public void setcenter_banner(String center_banner) {
        this.center_banner = center_banner;
    }

    /**
     * @return The center_address
     */
    public String getcenter_address() {
        return center_address;
    }

    /**
     * @param center_address The center_address
     */
    public void setcenter_address(String center_address) {
        this.center_address = center_address;
    }

    /**
     * @return The pia_Name
     */
    public String getpia_Name() {
        return pia_Name;
    }

    /**
     * @param pia_Name The pia_Name
     */
    public void setpia_Name(String pia_Name) {
        this.pia_Name = pia_Name;
    }

    /**
     * @return The created_by
     */
    public String getcreated_by() {
        return created_by;
    }

    /**
     * @param created_by The created_by
     */
    public void setcreated_by(String created_by) {
        this.created_by = created_by;
    }

    /**
     * @return The created_at
     */
    public String getcreated_at() {
        return created_at;
    }

    /**
     * @param created_at The created_at
     */
    public void setcreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * @return The updated_by
     */
    public String getupdated_by() {
        return updated_by;
    }

    /**
     * @param updated_by The updated_by
     */
    public void setupdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    /**
     * @return The updated_at
     */
    public String getupdated_at() {
        return updated_at;
    }

    /**
     * @param updated_at The updated_at
     */
    public void setupdated_at(String updated_at) {
        this.updated_at = updated_at;
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