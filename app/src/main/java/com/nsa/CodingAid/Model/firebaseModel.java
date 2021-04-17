package com.nsa.CodingAid.Model;

import java.util.ArrayList;

public class firebaseModel {
    String name;
    PlatformInfo platform;
    String connectedTeacher;
    String id;
    String token;
    boolean verified;
    ArrayList<String> fields;

    public firebaseModel() {
    }

    public firebaseModel(String name, PlatformInfo platform, String connectedTeacher, String id, String token, boolean verified, ArrayList<String> fields) {
        this.name = name;
        this.platform = platform;
        this.connectedTeacher = connectedTeacher;
        this.id = id;
        this.token = token;
        this.verified = verified;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlatformInfo getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformInfo platform) {
        this.platform = platform;
    }

    public String getConnectedTeacher() {
        return connectedTeacher;
    }

    public void setConnectedTeacher(String connectedTeacher) {
        this.connectedTeacher = connectedTeacher;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public void setFields(ArrayList<String> fields) {
        this.fields = fields;
    }
}
