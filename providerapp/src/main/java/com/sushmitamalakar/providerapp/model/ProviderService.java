package com.sushmitamalakar.providerapp.model;

public class ProviderService {
    private String providerId;
    private String serviceId;
    private double charge;

    // Default constructor required for calls to DataSnapshot.getValue(ProviderService.class)
    public ProviderService() {
    }

    public ProviderService(String providerId, String serviceId, double charge) {
        this.providerId = providerId;
        this.serviceId = serviceId;
        this.charge = charge;
    }

    // Getters and setters
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }
}


