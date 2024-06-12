package com.mintype.chatapp;

import com.google.firebase.Timestamp;

public class Message {
    private String sender;
    private String message;
    private String userID;
    private Timestamp timestamp;

    public Message() {
        // Required for Firestore serialization
    }

    public Message(String message) {
        this.sender = "server";
        this.message = message;
        this.timestamp = Timestamp.now();
    }

    public Message(String sender, String message, Timestamp timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Message(String userId, String sender, String message, Timestamp timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.userID = userId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
