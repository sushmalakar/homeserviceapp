package com.sushmitamalakar.providerapp.model;


public class Document {
    private String frontImageUrl;
    private String backImageUrl;
    private String status;
//    private String citizenshipNumber;
//    private String issueDate;
//    private String issueDistrict;

    public Document() {
    }

    public Document(String frontImageUrl, String backImageUrl) {
        this.frontImageUrl = frontImageUrl;
        this.backImageUrl = backImageUrl;
        this.status = "Pending";

//        this.citizenshipNumber = citizenshipNumber;
//        this.issueDate = issueDate;
//        this.issueDistrict = issueDistrict;
    }

    public String getFrontImageUrl() {
        return frontImageUrl;
    }

    public void setFrontImageUrl(String frontImageUrl) {
        this.frontImageUrl = frontImageUrl;
    }

    public String getBackImageUrl() {
        return backImageUrl;
    }

    public void setBackImageUrl(String backImageUrl) {
        this.backImageUrl = backImageUrl;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public String getCitizenshipNumber() {
//        return citizenshipNumber;
//    }
//
//    public void setCitizenshipNumber(String citizenshipNumber) {
//        this.citizenshipNumber = citizenshipNumber;
//    }
//
//    public String getIssueDate() {
//        return issueDate;
//    }
//
//    public void setIssueDate(String issueDate) {
//        this.issueDate = issueDate;
//    }
//
//    public String getIssueDistrict() {
//        return issueDistrict;
//    }
//
//    public void setIssueDistrict(String issueDistrict) {
//        this.issueDistrict = issueDistrict;
//    }
}
