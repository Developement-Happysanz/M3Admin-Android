package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProspectDoc  implements Serializable {
    
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("doc_proof_number")
    @Expose
    private String doc_proof_number;

    @SerializedName("file_name")
    @Expose
    private String file_name;

    @SerializedName("doc_name")
    @Expose
    private String doc_name;

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
     * @return The doc_proof_number
     */
    public String getdoc_proof_number() {
        return doc_proof_number;
    }

    /**
     * @param doc_proof_number The doc_proof_number
     */
    public void setdoc_proof_number(String doc_proof_number) {
        this.doc_proof_number = doc_proof_number;
    }

    /**
     * @return The file_name
     */
    public String getfile_name() {
        return file_name;
    }

    /**
     * @param file_name The file_name
     */
    public void setfile_name(String file_name) {
        this.file_name = file_name;
    }

    /**
     * @return The doc_name
     */
    public String getdoc_name() {
        return doc_name;
    }

    /**
     * @param doc_name The doc_name
     */
    public void setdoc_name(String doc_name) {
        this.doc_name = doc_name;
    }
}