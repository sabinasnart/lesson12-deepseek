package com.amicus.myapplication;

import java.util.List;

/**
 * Created by sizik on 27.09.2025.
 */
public class ChatResponse {
    List<Choice> choices;

    public static class Choice{
        public Message message;
    }

    public static class Message{
        String role;
        String content;
    }
}
