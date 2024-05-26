package com.mintype.chatapp;

public class ChatMessage {
    public String message;
    private String collectionName;


    public ChatMessage(String collectionName) {
        this.collectionName = collectionName;
        message = "Welcome to room: " + this.collectionName + "!";
    }
}
