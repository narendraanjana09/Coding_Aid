package com.nsa.CodingAid.Model;

import java.util.ArrayList;

public class FirebaseModel {
    String name;
    PlatformInfo platform;
    String connectedTeacher;
    String uid;
    String dateOfJoining;
    String token;
    boolean verified;
    ArrayList<String> fields;

    public FirebaseModel() {
    }

    public FirebaseModel(String name, PlatformInfo platform, String connectedTeacher, String uid, String dateOfJoining, String token, boolean verified, ArrayList<String> fields) {
        this.name = name;
        this.platform = platform;
        this.connectedTeacher = connectedTeacher;
        this.uid = uid;
        this.dateOfJoining = dateOfJoining;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
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