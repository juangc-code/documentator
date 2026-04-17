package com.juangccode.documentator.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class AgentConfig {

    @Bean
    public ChatOptions chatOptions() {
        return OpenAiChatOptions.builder()
                .extraBody(
                        Map.of(
                                "thinking", Map.of("type", "disabled")))
                .build();
    }

    @Bean
    @Qualifier("kimiChatModel")
    public ChatModel kimiChatModel(OpenAiChatModel chatModel) {
        return chatModel;
    }

    @Bean
    @Qualifier("geminiChatModel")
    public ChatModel geminiChatModel(GoogleGenAiChatModel chatModel) {
        return chatModel;
    }

    @Bean
    @Qualifier("kimiChatClient")
    public ChatClient kimiChatClient(
            @Qualifier("kimiChatModel") ChatModel chatModel,
            SyncMcpToolCallbackProvider toolCallbackProvider,
            ChatOptions chatOptions) {
        return ChatClient.builder(chatModel)
                .defaultOptions(chatOptions)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();
    }

    @Bean
    @Qualifier("geminiChatClient")
    public ChatClient geminiChatClient(
            @Qualifier("geminiChatModel") ChatModel chatModel,
            SyncMcpToolCallbackProvider toolCallbackProvider) {
        return ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider.getToolCallbacks())
                .build();
    }
}
