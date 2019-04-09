package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MobilizationPlan implements Serializable {

    @SerializedName("plan_id")
    @Expose
    private String planId;

    @SerializedName("doc_name")
    @Expose
    private String docName;

    @SerializedName("doc_month_year")
    @Expose
    private String docMonthYear;

    @SerializedName("doc_url")
    @Expose
    private String docUrl;

    /**
     * @return The planId
     */
    public String getPlanId() {
        return planId;
    }

    /**
     * @param planId The planId
     */
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    /**
     * @return The docName
     */
    public String getDocName() {
        return docName;
    }

    /**
     * @param docName The docName
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * @return The docMonthYear
     */
    public String getDocMonthYear() {
        return docMonthYear;
    }

    /**
     * @param docMonthYear The docMonthYear
     */
    public void setDocMonthYear(String docMonthYear) {
        this.docMonthYear = docMonthYear;
    }

    /**
     * @return The docUrl
     */
    public String getDocUrl() {
        return docUrl;
    }

    /**
     * @param docUrl The docUrl
     */
    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }
}