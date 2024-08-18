package edu.divyagyan.homeserviceadmin.model;

public class Service {
    private String serviceImage;
    private String serviceTitle;
//    private Double servicePrice;

    public Service(String serviceImage, String serviceTitle) {
        this.serviceImage = serviceImage;
        this.serviceTitle = serviceTitle;
//        this.servicePrice = servicePrice;
    }
    public  Service(){}

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

//    public Double getServicePrice() {
//        return servicePrice;
//    }
//
//    public void setServicePrice(Double servicePrice) {
//        this.servicePrice = servicePrice;
//    }
}
