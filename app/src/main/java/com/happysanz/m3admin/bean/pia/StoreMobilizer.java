package com.happysanz.m3admin.bean.pia;

public class StoreMobilizer {

    private String mobilizerId;
    private String mobilizerName;

    public StoreMobilizer(String mobilizerId, String mobilizerName) {
        this.mobilizerId = mobilizerId;
        this.mobilizerName = mobilizerName;
    }

    public String getMobilizerId() {
        return mobilizerId;
    }

    public void setMobilizerId(String mobilizerId) {
        this.mobilizerId = mobilizerId;
    }

    public String getMobilizerName() {
        return mobilizerName;
    }

    public void setMobilizerName(String mobilizerName) {
        this.mobilizerName = mobilizerName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return mobilizerName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StoreMobilizer){
            StoreMobilizer c = (StoreMobilizer )obj;
            if(c.getMobilizerName().equals(mobilizerName) && c.getMobilizerId().equals(mobilizerId)) return true;
        }

        return false;
    }
}