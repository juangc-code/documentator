package com.juangccode.documentator.tool;

import com.juangccode.documentator.service.GitHubService;
import com.juangccode.documentator.service.ShellService;
import com.juangccode.documentator.util.WorkspaceContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class GitTool {

    private static final Logger LOG = Logger.getLogger(GitTool.class.getName());

    private final WorkspaceContext workspaceContext;
    private final ShellService shellService;
    private final GitHubService gitHubService;
    @Value("${github.token}")
    private String token;


    public GitTool(WorkspaceContext workspaceContext,
                   ShellService shellService,
                   GitHubService gitHubService) {
        this.workspaceContext = workspaceContext;
        this.shellService = shellService;
        this.gitHubService = gitHubService;
    }


    @Tool(description = "Clone a repository into the current directory")
    public String clone(String repositoryUrl) {
        LOG.info("Cloning repository...");
        var path = repositoryUrl.substring(repositoryUrl.lastIndexOf('/') + 1)
                .replace(".git", "");
        var root = "/repo_dir/" + path;
        LOG.info("Workspace set to: " + root);
        var url = buildUrl(repositoryUrl);

        var clone = List.of("git", "clone", url, root);
        var isSuccess = shellService.execute(clone, root).isSuccess();
        if (!isSuccess) {
            return null;
        }
        configureRepo(repositoryUrl);
        workspaceContext.setCurrentWorkspace(root);
        return root;
    }

    @Tool(description = "Checkout to the specified branch.")
    public String checkout(String branch) {
        LOG.info("Checkout to branch...");
        var root = workspaceContext.getCurrentWorkspace();
        var command = List.of("git", "checkout", branch);
        return shellService.execute(command, root).output();
    }

    @Tool(description = "Checkout to the specified new branch based on current branch.")
    public String checkoutNewBranch(String branch) {
        LOG.info("Checkout to new branch...");
        var root = workspaceContext.getCurrentWorkspace();
        var command = List.of("git", "checkout", "-b", branch);
        return shellService.execute(command, root).output();
    }

    @Tool(description = "Stage all changes")
    public String stage() {
        LOG.info("Staging all changes...");
        var root = workspaceContext.getCurrentWorkspace();
        var command = List.of("git", "add", ".");
        return shellService.execute(command, root).output();
    }

    @Tool(description = "Stage a list of changes")
    public String stageList(List<String> changes) {
        LOG.info("Staging list of changes...");
        var root = workspaceContext.getCurrentWorkspace();
        var command = List.of("git", "add", String.join(" ", changes));
        return shellService.execute(command, root).output();
    }

    @Tool(description = "Commit changes")
    public String commit(String commitMessage) {
        LOG.info("Committing changes...");
        var root = workspaceContext.getCurrentWorkspace();
        var command = List.of("git", "commit", "-m", commitMessage);
        return shellService.execute(command, root).output();
    }

    @Tool(description = "Push changes to origin branch")
    public String push(String branch) {
        LOG.info("Pushing changes...");
        var root = workspaceContext.getCurrentWorkspace();
        var command = List.of("git", "push", "origin", branch);
        return shellService.execute(command, root).output();
    }

    @Tool(description = "Pull changes from origin branch")
    public String pull(String branch) {
        var root = workspaceContext.getCurrentWorkspace();
        LOG.info("Pulling changes...");
        var command = List.of("git", "pull", "origin", branch);
        return shellService.execute(command, root).output();
    }

    @Tool(description = "Create a pull request")
    public Map createPullRequest(String owner,
                                 String head) {
        var root = workspaceContext.getCurrentWorkspace();
        LOG.info("Creating pull request...");
        return gitHubService.openPullRequest(
                owner,
                root,
                "Add diagrams",
                head,
                "main",
                "{}");
    }

    @Tool(description = "List all files in the repository folder")
    public List<String> listFiles() {
        var root = workspaceContext.getCurrentWorkspace();
        LOG.info("Listing all files...");
        var command = List.of("git", "ls-files");
        var result = shellService.execute(command, root);
        if (result == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(result.output().split(" ")).toList();
    }

    @Tool(description = "Search for text in the repository and return matching lines with file paths")
    public String search(String query) {
        var root = workspaceContext.getCurrentWorkspace();
        LOG.info("Searching for text...");
        var command = List.of("rg", "-n", "--no-heading", query);
        var result = shellService.execute(command, root);
        return result.output();
    }

    @Tool(description = "Read a portion of a file given a relative path, start line, and end line")
    public String readFile(String relativePath, int startLine, int endLine) {
        var root = workspaceContext.getCurrentWorkspace();
        LOG.info("Reading file...");
        var command = List.of("sed", "-n", "'" + startLine + "," + endLine + "'", relativePath);
        return shellService.execute(command, root).output();
    }

    @Tool(description = "Get a quick summary of a file (first 200 lines)")
    public String previewFile(String relativePath) {
        var root = workspaceContext.getCurrentWorkspace();
        LOG.info("Quick summary of a file...");
        var command = List.of("head", "-n", "200", relativePath);
        return shellService.execute(command, root).output();
    }

    @Tool(description = "Show directory tree up to a certain depth")
    public String tree(int depth) {
        var root = workspaceContext.getCurrentWorkspace();
        LOG.info("Show directory tree...");
        var command = List.of("tree", "-L", String.valueOf(depth));
        return shellService.execute(command, root).output();
    }


    private String buildUrl(String repositoryUrl) {
        var url = "https://" + token + "@" + repositoryUrl.split("https://")[1];
        LOG.info("Result URL: " + url);
        return url;
    }

    private void configureRepo(String repo) {
        var repoPath = repo.substring(repo.lastIndexOf('/') + 1)
                .replace(".git", "");
        shellService.execute(List.of("git", "-C", repoPath, "remote", "set-url", "origin", buildUrl(repo)), ".");
        shellService.execute(List.of("git", "-C", repoPath, "config", "user.email", "bothemail@example.com"), ".");
        shellService.execute(List.of("git", "-C", repoPath, "config", "user.name", "spring-ai-bot"), ".");
    }
}
