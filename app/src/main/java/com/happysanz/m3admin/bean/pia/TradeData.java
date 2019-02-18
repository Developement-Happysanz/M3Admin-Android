package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 08-01-2018.
 */

public class TradeData implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("trade_name")
    @Expose
    private String tradeName;

    @SerializedName("status")
    @Expose
    private String status;

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
     * @return The tradeName
     */
    public String getTradeName() {
        return tradeName;
    }

    /**
     * @param tradeName The tradeName
     */
    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
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
