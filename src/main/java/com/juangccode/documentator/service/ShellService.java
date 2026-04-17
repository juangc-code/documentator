package com.juangccode.documentator.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ShellService {

    private static final long DEFAULT_TIMEOUT_SECONDS = 40;

    public CommandResult execute(List<String> command, String workingDir) {
        return execute(command, workingDir, DEFAULT_TIMEOUT_SECONDS);
    }

    public CommandResult execute(List<String> command, String workingDir, long timeoutSeconds) {
        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(workingDir));
            pb.redirectErrorStream(true);

            process = pb.start();

            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                return new CommandResult(-1, "Process timed out after " + timeoutSeconds + " seconds");
            }

            String output = new String(process.getInputStream().readAllBytes());
            int exitCode = process.exitValue();

            return new CommandResult(exitCode, output);
        } catch (Exception e) {
            if (process != null) process.destroyForcibly();
            return new CommandResult(-1, e.getMessage());
        }
    }

    public record CommandResult(int exitCode, String output) {
        public boolean isSuccess() { return exitCode == 0; }
    }
}
