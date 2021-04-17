package com.nsa.CodingAid.Model;

public class fieldWithID {
    public String field;
    public String id;

    public fieldWithID() {
    }

    public fieldWithID(String field, String id) {
        this.field = field;
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
