package com.sushmitamalakar.homeserviceapp.model;

public class Service {
    private String serviceId;
    private String serviceImage;
    private String serviceTitle;

    public Service(String serviceId, String serviceImage, String serviceTitle) {
        this.serviceId = serviceId;
        this.serviceImage = serviceImage;
        this.serviceTitle = serviceTitle;
    }

    public Service() {}

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
