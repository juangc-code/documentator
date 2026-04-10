package com.juangccode.documentator.util;

import org.springframework.stereotype.Component;

@Component
public class WorkspaceContext {

    private String currentWorkspace;

    public String getCurrentWorkspace() {
        return currentWorkspace;
    }

    public void setCurrentWorkspace(String currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
    }


}
