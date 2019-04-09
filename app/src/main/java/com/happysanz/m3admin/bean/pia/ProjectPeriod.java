package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProjectPeriod implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("period_from")
    @Expose
    private String periodFrom;

    @SerializedName("period_to")
    @Expose
    private String periodTo;

    @SerializedName("pia_id")
    @Expose
    private String piaId;

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
     * @return The periodFrom
     */
    public String getPeriodFrom() {
        return periodFrom;
    }

    /**
     * @param periodFrom The periodFrom
     */
    public void setPeriodFrom(String periodFrom) {
        this.periodFrom = periodFrom;
    }

    /**
     * @return The periodTo
     */
    public String getPeriodTo() {
        return periodTo;
    }

    /**
     * @param periodTo The periodTo
     */
    public void setPeriodTo(String periodTo) {
        this.periodTo = periodTo;
    }

    /**
     * @return The piaId
     */
    public String getPiaId() {
        return piaId;
    }

    /**
     * @param piaId The piaId
     */
    public void setPiaId(String piaId) {
        this.piaId = piaId;
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