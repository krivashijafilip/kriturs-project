package com.bala.kriturs;

public class Cruise {
    private String cruiseName;
    private String cruiseDescription;
    private String cruiseImage;
    private String cruisePrice;

    public Cruise() {
        // Required empty constructor for Firestore
    }

    public Cruise(String cruiseName, String cruiseDescription, String cruiseImage, String cruisePrice) {
        this.cruiseName = cruiseName;
        this.cruiseDescription = cruiseDescription;
        this.cruiseImage = cruiseImage;
        this.cruisePrice = cruisePrice;
    }

    public String getCruiseName() {
        return cruiseName;
    }

    public String getCruiseDescription() {
        return cruiseDescription;
    }

    public String getCruiseImage() {
        return cruiseImage;
    }

    public String getCruisePrice() {
        return cruisePrice;
    }
}

