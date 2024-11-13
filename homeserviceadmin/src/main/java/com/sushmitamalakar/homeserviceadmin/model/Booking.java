package com.sushmitamalakar.homeserviceadmin.model;

public class Booking {
    private String charge, date, message, providerId, serviceId, status, time, userId;
    private String serviceImage, serviceTitle, userName, bookingId;


    public Booking() {
    }

    // Getters and Setters
    public String getCharge() { return charge; }
    public void setCharge(String charge) { this.charge = charge; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getServiceImage() { return serviceImage; }
    public void setServiceImage(String serviceImage) { this.serviceImage = serviceImage; }

    public String getServiceTitle() { return serviceTitle; }
    public void setServiceTitle(String serviceTitle) { this.serviceTitle = serviceTitle; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getBookingId() {return bookingId; }

    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

}
