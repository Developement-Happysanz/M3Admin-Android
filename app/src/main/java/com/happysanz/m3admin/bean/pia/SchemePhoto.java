package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SchemePhoto  implements Serializable {

    @SerializedName("scheme_photo")
    @Expose
    private String centerPhotos;

    @SerializedName("gallery_id")
    @Expose
    private String galleryId;

    /**
     * @return The centerPhotos
     */
    public String getCenterPhotos() {
        return centerPhotos;
    }

    /**
     * @param centerPhotos The centerPhotos
     */
    public void setCenterPhotos(String centerPhotos) {
        this.centerPhotos = centerPhotos;
    }

    /**
     * @return The galleryId
     */
    public String getGalleryId() {
        return galleryId;
    }

    /**
     * @param galleryId The galleryId
     */
    public void setGalleryId(String galleryId) {
        this.galleryId = galleryId;
    }
}
