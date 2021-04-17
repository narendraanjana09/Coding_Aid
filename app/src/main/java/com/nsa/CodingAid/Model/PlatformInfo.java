package com.nsa.CodingAid.Model;

public class PlatformInfo {
    private String platform_name;
    private String username;

    public PlatformInfo() {
    }

    public PlatformInfo(String platform_name, String username) {
        this.platform_name = platform_name;
        this.username = username;
    }

    public String getPlatform_name() {
        return platform_name;
    }

    public void setPlatform_name(String platform_name) {
        this.platform_name = platform_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
