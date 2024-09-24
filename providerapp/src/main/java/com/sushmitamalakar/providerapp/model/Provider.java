package com.sushmitamalakar.providerapp.model;

public class Provider {
    private String fullName;
    private String email;
    private String mobileNo;
    private String password;
    private String confirmPassword;
    private String imageUrl;

    public Provider(){}

    public Provider(String fullName, String email, String mobileNo, String imageUrl) {
        this.fullName = fullName;
        this.email = email;
        this.mobileNo = mobileNo;
        this.imageUrl = imageUrl;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
