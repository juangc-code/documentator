package com.juangccode.documentator.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ShellService {

    public CommandResult execute(List<String> command, String workingDir) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(workingDir));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();

            return new CommandResult(exitCode, output);
        } catch (Exception e) {
            return new CommandResult(-1, e.getMessage());
        }
    }

    public record CommandResult(int exitCode, String output) {
        public boolean isSuccess() { return exitCode == 0; }
    }
}
