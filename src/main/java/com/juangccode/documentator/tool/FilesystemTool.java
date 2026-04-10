package com.juangccode.documentator.tool;

import com.juangccode.documentator.service.ShellService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilesystemTool {

    private final ShellService shellService;

    public FilesystemTool(ShellService shellService) {
        this.shellService = shellService;
    }

    @Tool(description = "Change to a directory")
    public void changeDirectory(String directory) {
        var cd = List.of("cd", directory);
        shellService.execute(cd, ".");
    }

    @Tool(description = "Return the path of the current directory")
    public String currentDirectory() {
        var pwd = List.of("pwd");
        var result = shellService.execute(pwd, ".");
        return result.output();
    }
}
