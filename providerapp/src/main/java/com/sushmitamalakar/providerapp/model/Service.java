package com.sushmitamalakar.providerapp.model;

public class Service {
    private String serviceImage;
    private String serviceTitle;

    public Service(String serviceImage, String serviceTitle) {
        this.serviceImage = serviceImage;
        this.serviceTitle = serviceTitle;
    }

    public Service() {}

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
