package com.sushmitamalakar.homeserviceapp.model;

public class User {
    private String fullName;
    private String email;
    private String mobileNo;
    private String password;
    private String confirmPassword;

    public User(){}

    public User(String fullName, String email, String mobileNo) {
        this.fullName = fullName;
        this.email = email;
        this.mobileNo = mobileNo;

    }

    // Getters and setters

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



    // Validation methods
//    public boolean isValid() {
//        // Check if any field is null or empty
//        if (fullName == null || fullName.isEmpty()) return false;
//        if (email == null || email.isEmpty()) return false;
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false;
//        if (mobileNo == null || mobileNo.isEmpty()) return false;
//        if (mobileNo.length() != 10) return false;
//        if (password == null || password.isEmpty()) return false;
//        if (password.length() < 6) return false;
//        if (!password.equals(confirmPassword)) return false;
//
//        return true;
//
//    }
}
