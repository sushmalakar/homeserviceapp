package com.sushmitamalakar.homeserviceapp.model;

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
        private String shortAddress; // Add this field

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

        public String getShortAddress() {
            return shortAddress;
        }

        public void setShortAddress(String shortAddress) {
            this.shortAddress = shortAddress;
        }

        @Override
        public String toString() {
            return shortAddress != null ? shortAddress : latitude + ", " + longitude;
        }
    }
}
