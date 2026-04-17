package com.juangccode.documentator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class GitHubService {

    private final RestClient gitHubRestClient;

    public GitHubService(RestClient gitHubRestClient) {
        this.gitHubRestClient = gitHubRestClient;
    }


    public Map openPullRequest(String owner,
                               String repo,
                               String title,
                               String head,
                               String base,
                               String body) {
        Map<String, String> payload = Map.of(
                "title", title,
                "head",  head,   // your branch
                "base",  base,   // target branch, e.g. "main"
                "body",  body
        );

        return gitHubRestClient.post()
                .uri("/repos/{owner}/{repo}/pulls", owner, repo)
                .body(payload)
                .retrieve()
                .body(Map.class);
    }

}
