package com.sushmitamalakar.homeserviceadmin.model;

public class Service {

    private String serviceId;    // Unique service ID
    private String serviceImage; // URL of the service image
    private String serviceTitle; // Title of the service

    // Default constructor required for calls to DataSnapshot.getValue(Service.class)
    public Service() {
    }

    // Constructor with parameters
    public Service(String serviceId, String serviceImage, String serviceTitle) {
        this.serviceId = serviceId;
        this.serviceImage = serviceImage;
        this.serviceTitle = serviceTitle;
    }

    // Getters and setters
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }
}
