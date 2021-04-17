package com.nsa.CodingAid.Model;

import java.util.List;

public class availableFieldModel {
    private String fieldName;
    private List<String> avaiableList;

    public availableFieldModel() {
    }

    public availableFieldModel(String fieldName, List<String> avaiableList) {
        this.fieldName = fieldName;
        this.avaiableList = avaiableList;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<String> getAvaiableList() {
        return avaiableList;
    }

    public void setAvaiableList(List<String> avaiableList) {
        this.avaiableList = avaiableList;
    }
}
