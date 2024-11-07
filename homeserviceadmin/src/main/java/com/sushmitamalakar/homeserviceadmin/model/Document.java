package com.sushmitamalakar.homeserviceadmin.model;

public class Document {
    private String providerId;
    private String providerName;  // New field for provider name
    private String documentId;
    private String frontImageUrl;
    private String backImageUrl;
    private String status;

    // Default constructor required for calls to DataSnapshot.getValue(Document.class)
    public Document() {
    }

    // Constructor with all fields
    public Document(String providerId, String providerName, String documentId, String frontImageUrl, String backImageUrl, String status) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.documentId = documentId;
        this.frontImageUrl = frontImageUrl;
        this.backImageUrl = backImageUrl;
        this.status = status;
    }

    // Getter and Setter methods
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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
}
