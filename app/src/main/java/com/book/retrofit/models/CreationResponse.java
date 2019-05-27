package com.book.retrofit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreationResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("dateCreate")
    @Expose
    private String dateCreate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

}
