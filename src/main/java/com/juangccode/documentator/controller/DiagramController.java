package com.juangccode.documentator.controller;

import com.juangccode.documentator.service.DiagramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiagramController {

    private final DiagramService diagramService;

    public DiagramController(DiagramService diagramService) {
        this.diagramService = diagramService;
    }

    @PostMapping
    public ResponseEntity<String> postMessage(@RequestBody String message){
        var response = diagramService.generateDiagram(message);

        return ResponseEntity.ok(response);
    }
}
