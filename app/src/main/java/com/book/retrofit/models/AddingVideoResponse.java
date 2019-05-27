package com.book.retrofit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddingVideoResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("object")
    @Expose
    private Object object;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public class Object {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("videoGeneralId")
        @Expose
        private Integer videoGeneralId;
        @SerializedName("dateCreate")
        @Expose
        private String dateCreate;
        @SerializedName("path")
        @Expose
        private String path;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getVideoGeneralId() {
            return videoGeneralId;
        }

        public void setVideoGeneralId(Integer videoGeneralId) {
            this.videoGeneralId = videoGeneralId;
        }

        public String getDateCreate() {
            return dateCreate;
        }

        public void setDateCreate(String dateCreate) {
            this.dateCreate = dateCreate;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

    }
}
