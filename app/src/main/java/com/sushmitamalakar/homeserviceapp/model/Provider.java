package com.sushmitamalakar.homeserviceapp.model;

//public class Provider {
//    private String providerId;
//    private String charge;
//
//    public Provider() {}
//
//    public Provider(String providerId, String charge) {
//        this.providerId = providerId;
//        this.charge = charge;
//    }
//
//    public String getProviderId() {
//        return providerId;
//    }
//
//    public void setProviderId(String providerId) {
//        this.providerId = providerId;
//    }
//
//    public String getCharge() {
//        return charge;
//    }
//
//    public void setCharge(String charge) {
//        this.charge = charge;
//    }
//}


public class Provider {
    private String providerId;
    private String fullName;
    private String charge;
    private Location location; // Nested class for latitude & longitude
    private String imageUrl;

    public Provider() {}

    public Provider(String providerId, String fullName, String charge, Location location, String imageUrl) {
        this.providerId = providerId;
        this.fullName = fullName;
        this.charge = charge;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCharge() {
        return charge;
    }

    public Location getLocation() {
        return location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Nested Location class for latitude and longitude
    public static class Location {
        private double latitude;
        private double longitude;

        public Location() {}

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        @Override
        public String toString() {
            return latitude + ", " + longitude;
        }
    }
}
