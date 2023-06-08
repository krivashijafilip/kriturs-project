package com.bala.kriturs;

public class User {
    private String userID;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }
}
