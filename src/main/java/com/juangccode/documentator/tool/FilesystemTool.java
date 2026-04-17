package com.juangccode.documentator.tool;

import com.juangccode.documentator.service.ShellService;
import com.juangccode.documentator.util.WorkspaceContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class FilesystemTool {

    private static final Logger LOG = Logger.getLogger(FilesystemTool.class.getName());

    private final WorkspaceContext workspaceContext;
    private final ShellService shellService;

    public FilesystemTool(WorkspaceContext workspaceContext,
                          ShellService shellService) {
        this.workspaceContext = workspaceContext;
        this.shellService = shellService;
    }

    @Tool(description = "Change to a directory")
    public void cd(String path) {
        LOG.info("Changing to directory: " + path);
        var root = workspaceContext.getCurrentWorkspace().getAbsolutePath();
        var cd = List.of("cd", path);
        shellService.execute(cd, root);
        workspaceContext.setCurrentWorkspace(path);
    }

    @Tool(description = "Return the path of the current directory")
    public String pwd() {
        var path = workspaceContext.getCurrentWorkspace().getAbsolutePath();
        LOG.info("Current directory: " + path);
        return path;
    }

    @Tool(description = "Create a directory")
    public String mkdir(String directoryName) {
        LOG.info("Creating directory: " + directoryName);
        var root = workspaceContext.getCurrentWorkspace().getAbsolutePath();
        var cd = List.of("mkdir", "-p", directoryName);
        shellService.execute(cd, root);
        return "Directory " + directoryName + " created";
    }

    @Tool(description = "Write content to a file at the given path. If the path is relative," +
            " it is resolved against the current workspace.")
    public String writeFile(
            @ToolParam(description = "Absolute or relative path to the file") String path,
            @ToolParam(description = "Content to write into the file") String content
    ) {
        try {
            Path filePath = Path.of(path);
            // Resolve relative paths against the current workspace
            if (!filePath.isAbsolute()) {
                String workspace = workspaceContext.getCurrentWorkspace().getAbsolutePath();
                filePath = Path.of(workspace).resolve(filePath);
            }
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content);
            LOG.info("File written successfully: " + filePath);
            return "File written successfully: " + filePath.toAbsolutePath();
        } catch (IOException e) {
            LOG.severe("Error writing file: " + e.getMessage());
            return "Error writing file: " + e.getMessage();
        }
    }

    @Tool(description = "Read all the content of a file at the given path.")
    public String readFullFile(
            @ToolParam(description = "Path to the file to read") String path
    ) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool(description = "List files and directories at the given path.")
    public String listDirectory(
            @ToolParam(description = "Path to the directory") String path
    ) {
        try (var stream = Files.list(Path.of(path))) {
            return stream
                    .map(p -> (Files.isDirectory(p) ? "[DIR] " : "[FILE] ") + p.getFileName())
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return "Error listing directory: " + e.getMessage();
        }
    }

}
