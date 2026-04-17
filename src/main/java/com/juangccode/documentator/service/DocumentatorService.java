package com.juangccode.documentator.service;

import com.juangccode.documentator.tool.FilesystemTool;
import com.juangccode.documentator.tool.GitTool;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DocumentatorService {

    private final ChatClient chatClient;
    private final GitTool gitTool;
    private final FilesystemTool filesystemTool;

    public DocumentatorService(
            @Qualifier("kimiChatClient") ChatClient chatClient,
//            @Qualifier("geminiChatClient") ChatClient chatClient,
            GitTool gitTool,
            FilesystemTool filesystemTool) {
        this.chatClient = chatClient;
        this.gitTool = gitTool;
        this.filesystemTool = filesystemTool;
    }


    @RateLimiter(name = "kimiApi")
    @Retry(name = "kimiApi")
    public String generateDiagram(String repositoryUrl) {
        return chatClient.prompt("""
                                You are a software architect analyzing a code repository.
                        
                                  ## Setup
                                  1. Clone repository: %s into /repo_dir. Do this ONCE.
                                  2. Identify the repository name from the cloned folder. Remember this as REPO_NAME.
                                  3. The cloned repository is at: /repo_dir/REPO_NAME — use this absolute path for all subsequent file operations.
                                  4. Checkout a new branch from current HEAD named "diagram-update".
                                  5. Create directories: /tmp/output/ and /repo_dir/REPO_NAME/docs

                                  ## Diagram Creation (using excalidraw-mcp tool)

                                  ### Required diagrams:
                                  - **architecture-overview.png** — High-level components, services, and their relationships
                                  - **endpoint-flows.png** — Request/response flow for the main endpoints (limit to top 5 if many exist)

                                  ### Conditional:
                                  - **erd.png** — Entity-relationship diagram, ONLY if the repo uses a database

                                  ### Excalidraw element rules (CRITICAL):
                                  - Valid element types: "rectangle", "ellipse", "diamond", "arrow", "line", "text", "frame"
                                  - Every element MUST have these fields: id (unique string), type, x, y, width, height, strokeColor, backgroundColor, fillStyle
                                  - Arrows connecting elements MUST include startBinding and endBinding with the target element's id
                                  - Use "strokeColor": "#1e1e2e" and "backgroundColor": "#ffffff" as defaults
                                  - Keep layouts readable: space elements at least 100px apart, use x/y ranges of 0–1200
                                  - Validate your JSON mentally before calling the tool — no trailing commas, no unquoted keys. Ensure no comments, no trailing commas, and proper quoting

                                  ## File Operations
                                  5. Save diagrams into /tmp/output
                                  6. Verify that each file exists by reading it back before proceeding.
                                  5. Copy all .png files from /tmp/output/*.png to /repo_dir/REPO_NAME/docs/*.png

                                  ## Git operations - DO NOT skip
                                  10. Call stage(path) on /repo_dir/REPO_NAME. Stage all changes
                                  12. Call commit("Add diagrams")
                                  13. Call push("diagram-update"). If push() returns an error, report it verbatim in the ERROR response.

                                  ## Output Format
                                  Respond ONLY with valid JSON:
                                  - Success: {"response": "SUCCESS", "diagrams": ["file1.png", ...]}
                                  - Failure: {"response": "ERROR", "details": "concise description of what failed and why"}

                                  ## Rules
                                  - Search before reading files; never read entire files unless necessary
                                  - Clone only once
                                  - If a required tool is unavailable, immediately return ERROR with details
                                  - Do not invent database usage — only create ERD if schema files or ORM models are found
                        """.formatted(repositoryUrl))
                .tools(gitTool, filesystemTool)
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();
    }

    @RateLimiter(name = "kimiApi")
    @Retry(name = "kimiApi")
    public String generateReadme(String repositoryUrl) {
        return chatClient.prompt("""
                                You are a software architect analyzing a code repository.
                        
                                  ## Setup
                                  1. Clone repository: %s into /repo_dir. Do this ONCE.
                                  2. Identify the repository name from the cloned folder. Remember this as REPO_NAME.
                                  3. The cloned repository is at: /repo_dir/REPO_NAME — use this absolute path for all subsequent file operations.
                                  4. Checkout a new branch from current HEAD named "readme-update".
                                  5. Create directory: /tmp/output/

                                  ## Readme Creation
                                  6. Analyze the repository and generate a README.md with:
                                  - Title & Short Description: The name of the project and a one-sentence summary of what it does
                                  - Installation: Clear, copy-pasteable commands for setting up dependencies and the local environment
                                  - Usage: Brief examples of how to run or use the tool, often with code snippets
                                  - Technologies Used: A list or set of badges (from Shields.io) showing the languages and frameworks used
                                  - Contributing: Guidelines for others who want to help
                                  - License: Information on how others can legally use or modify your code

                                  ## File Operations
                                  7. Write README.md to the absolute path: /repo_dir/REPO_NAME/README.md
                                  8. Verify the file exists by reading it back before proceeding.
                                  9. Copy README.md to /tmp/output/README.md

                                  ## Git operations - DO NOT skip
                                  10. Call stage(path) on /repo_dir/REPO_NAME
                                  12. Call commit("Add Readme")
                                  13. Call push("readme-update"). If push() returns an error, report it verbatim in the ERROR response.
                                  11. Commit with message: "Add Readme"
                                  12. Push to origin branch "readme-update".
                                  13. Verify the push succeeded. If it failed, report the exact error.

                                  ## Output Format
                                  Respond ONLY with valid JSON:
                                  - Success: {"response": "SUCCESS"}
                                  - Failure: {"response": "ERROR", "details": "concise description of what failed and why"}

                                  ## Rules
                                  - Search before reading files; never read entire files unless necessary
                                  - Clone only once
                                  - Always use ABSOLUTE paths when writing files
                                  - Do NOT proceed to git operations until you have verified README.md exists on disk
                                  - If a required tool is unavailable, immediately return ERROR with details
                                  - Your job is not over until you git push all changes to the repository.
                        """.formatted(repositoryUrl))
                .tools(gitTool, filesystemTool)
                .advisors(new SimpleLoggerAdvisor())
                .call()
                .content();
    }
}
