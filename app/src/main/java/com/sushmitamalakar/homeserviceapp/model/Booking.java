package com.sushmitamalakar.homeserviceapp.model;

public class Booking {
    private String serviceId;
    private String providerId;
    private String userId;
    private String serviceName;
    private String providerName;
    private String date;
    private String time;
    private String status;
    private String charge;

    // Required empty constructor for Firebase
    public Booking() {}

    // Add serviceName and providerName to the constructor if needed
    public Booking(String serviceId, String providerId, String userId, String serviceName, String providerName, String date, String time, String status, String charge) {
        this.serviceId = serviceId;
        this.providerId = providerId;
        this.userId = userId;
        this.serviceName = serviceName;
        this.providerName = providerName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.charge = charge;
    }

    // Getters and Setters
    public String getServiceId() { return serviceId; }
    public String getProviderId() { return providerId; }
    public String getUserId() { return userId; }
    public String getServiceName() { return serviceName; }
    public String getProviderName() { return providerName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getCharge() { return charge; }

    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
}
