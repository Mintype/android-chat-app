package com.mintype.chatapp;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Room {
    public String roomName;
    public ArrayList<Message> messages;
    public String userName;
    public String userId;

    public Room() {
        this.messages = new ArrayList<>();
    }

    public Room(String collectionName, String userName, String userId) {
        this.roomName = collectionName;
        this.userName = userName;
        this.userId = userId;
        this.messages = new ArrayList<>();
        this.messages.add(new Message(userName, "welcome to " + collectionName + ".", Timestamp.now()));
    }

    // Getter and Setter methods for userName and userId (optional)

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
