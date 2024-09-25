package com.sushmitamalakar.providerapp.model;


public class Document {
    private String frontImageUrl;
    private String backImageUrl;
    private String citizenshipNumber;
    private String issueDate;
    private String issueDistrict;

    public Document() {
        // Default constructor required for calls to DataSnapshot.getValue(Document.class)
    }

    public Document(String frontImageUrl, String backImageUrl, String citizenshipNumber, String issueDate, String issueDistrict) {
        this.frontImageUrl = frontImageUrl;
        this.backImageUrl = backImageUrl;
        this.citizenshipNumber = citizenshipNumber;
        this.issueDate = issueDate;
        this.issueDistrict = issueDistrict;
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

    public String getCitizenshipNumber() {
        return citizenshipNumber;
    }

    public void setCitizenshipNumber(String citizenshipNumber) {
        this.citizenshipNumber = citizenshipNumber;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueDistrict() {
        return issueDistrict;
    }

    public void setIssueDistrict(String issueDistrict) {
        this.issueDistrict = issueDistrict;
    }
}
