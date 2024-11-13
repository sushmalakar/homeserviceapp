package com.sushmitamalakar.homeserviceadmin.model;

public class User {
    private String userId;
    private String fullName;
    private String email;
    private String mobileNo; // Updated field name to match the Firebase database

    public User() {
        // Default constructor required for Firebase
    }

    public User(String userId, String fullName, String email, String mobileNo) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.mobileNo = mobileNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
