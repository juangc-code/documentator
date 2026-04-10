package com.juangccode.documentator.service;

import com.juangccode.documentator.tool.FilesystemTool;
import com.juangccode.documentator.tool.GitTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;

@Service
public class DiagramService {

    private final ChatClient chatClient;
    private final GitTool gitTool;
    private final FilesystemTool filesystemTool;

    public DiagramService(ChatClient chatClient,
                          GitTool gitTool,
                          FilesystemTool filesystemTool) {
        this.chatClient = chatClient;
        this.gitTool = gitTool;
        this.filesystemTool = filesystemTool;
    }


    public String generateDiagram(String repositoryUrl) {
        return chatClient.prompt("""
                                You are exploring a code repository.
                                
                                1 - Clone the repository %s and checkout to a new branch based on main
                                
                                2 - Create diagrams:
                                    - Create an overview architecture diagram
                                    - Create flow charts diagrams for all features
                                    - Create ERD diagrams if applicable
                                    - Export diagrams as PNG
                                
                                3 - Save the diagrams in /docs folder on repository (create if does not exists)
                                4 - Stage changes and commit
                                5 - Push changes to origin
                                6 - Create a pull request to main to remote origin
                                
                                Output:
                                - Response must be JSON format. Example: { "response": "SUCCESS"}
                                - Respond only "SUCCESS" (on success)
                                - Respond "ERROR" + summarize details of the errors (on errors). Example: {"response": "ERROR", "details": "Summarize error details"}
                                                
                                Rules:
                                - NEVER read entire files unless necessary
                                - ALWAYS search before reading
                                - Prefer small, targeted reads
                                - Iterate: search → read → refine
                                - Avoid redundant tool calls
                                - Clone repositories one time
                                - If you encounter any issues or necessary tools missing, stop processing and respond with "ERROR" and details
                                
                                Tools:
                                - You have Excalidraw tool for diagrams
                                - You have Git tool for git operations
                                - You have filesystem tool
                        """.formatted(repositoryUrl))
                .tools(gitTool, filesystemTool)
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();
    }
}
