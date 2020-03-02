package com.happysanz.m3admin.bean.tnsrlm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Scheme implements Serializable {

    @SerializedName("scheme_id")
    @Expose
    private String scheme_id;

    @SerializedName("scheme_name")
    @Expose
    private String scheme_name;

    /**
     * @return The scheme_id
     */
    public String getscheme_id() {
        return scheme_id;
    }

    /**
     * @param scheme_id The scheme_id
     */
    public void setscheme_id(String scheme_id) {
        this.scheme_id = scheme_id;
    }

    /**
     * @return The scheme_name
     */
    public String getscheme_name() {
        return scheme_name;
    }

    /**
     * @param scheme_name The scheme_name
     */
    public void setscheme_name(String scheme_name) {
        this.scheme_name = scheme_name;
    }

}