package com.juangccode.documentator.util;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class WorkspaceContext {

    private File currentWorkspace = new File("/");

    public File getCurrentWorkspace() {
        return currentWorkspace;
    }

    public synchronized void setCurrentWorkspace(String path) {
        try {
            File newDir = resolvePath(path);

            if (!newDir.exists() || !newDir.isDirectory()) {
                throw new IllegalArgumentException("Directory does not exist: " + path);
            }
            currentWorkspace = newDir.getCanonicalFile();
        } catch (Exception e) {
            throw new RuntimeException("Failed to set workspace: " + e.getMessage(), e);
        }
    }


    public synchronized File resolvePath(String path) {
        File file = new File(path);

        if (!file.isAbsolute()) {
            file = new File(currentWorkspace, path);
        }

        try {
            return file.getCanonicalFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
