package com.amicus.myapplication;

import java.util.List;

/**
 * Created by sizik on 27.09.2025.
 */
public class ChatRequest {
    String model;
    List<Message> messages;

    double temp;
    int max_token;

    public ChatRequest(String model, List<Message> messages, double temp, int max_token) {
        this.model = model;
        this.messages = messages;
        this.temp = temp;
        this.max_token = max_token;
    }

    public static class Message{
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
