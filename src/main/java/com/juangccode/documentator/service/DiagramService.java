package com.juangccode.documentator.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class DiagramService {

    private final ChatClient chatClient;

    public DiagramService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String generateDiagram(String description) {
        return chatClient.prompt()
                .user("""
                Create an architecture diagram for: %s
                Export it as PNG to /output/diagram.png
                """.formatted(description))
                .call()
                .content();
    }
}
