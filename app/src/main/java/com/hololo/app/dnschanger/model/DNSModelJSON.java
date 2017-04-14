package com.hololo.app.dnschanger.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DNSModelJSON {

    @SerializedName("modelList")
    @Expose
    private List<DNSModel> modelList;

    public List<DNSModel> getModelList() {
        return modelList;
    }

    public void setModelList(List<DNSModel> modelList) {
        this.modelList = modelList;
    }
}
