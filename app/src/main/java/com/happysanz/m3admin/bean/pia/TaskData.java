package com.happysanz.m3admin.bean.pia;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TaskData implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("task_title")
    @Expose
    private String taskTitle;

    @SerializedName("task_description")
    @Expose
    private String taskDescription;

    @SerializedName("task_date")
    @Expose
    private String taskDate;

    @SerializedName("pia_id")
    @Expose
    private String pia_id;

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
     * @return The taskTitle
     */
    public String getTaskTitle() {
        return taskTitle;
    }

    /**
     * @param taskTitle The taskTitle
     */
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    /**
     * @return The taskDescription
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * @param taskDescription The taskDescription
     */
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    /**
     * @return The taskDate
     */
    public String getTaskDate() {
        return taskDate;
    }

    /**
     * @param taskDate The taskDate
     */
    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
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
     * @return The pia_id
     */
    public String getPia_id() {
        return pia_id;
    }

    /**
     * @param pia_id The pia_id
     */
    public void setPia_id(String pia_id) {
        this.pia_id = pia_id;
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

    /**
     * @return The created_at
     */
    public String getCreated_at() {
        return created_at;
    }

    /**
     * @param created_at The created_at
     */
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * @return The created_by
     */
    public String getCreated_by() {
        return created_by;
    }

    /**
     * @param created_by The created_by
     */
    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    /**
     * @return The updated_at
     */
    public String getUpdated_at() {
        return updated_at;
    }

    /**
     * @param updated_at The updated_at
     */
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    /**
     * @return The updated_by
     */
    public String getUpdated_by() {
        return updated_by;
    }

    /**
     * @param updated_by The updated_by
     */
    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }
}