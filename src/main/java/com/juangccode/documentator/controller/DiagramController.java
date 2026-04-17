package com.juangccode.documentator.controller;

import com.juangccode.documentator.model.DiagramRequest;
import com.juangccode.documentator.service.DocumentatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiagramController {

    private final DocumentatorService documentatorService;

    public DiagramController(DocumentatorService documentatorService) {
        this.documentatorService = documentatorService;
    }

    @PostMapping
    public ResponseEntity<String> postMessage(@RequestBody DiagramRequest request){
        var response = documentatorService.generateDiagram(request.url());

        return ResponseEntity.ok(response);
    }
}
