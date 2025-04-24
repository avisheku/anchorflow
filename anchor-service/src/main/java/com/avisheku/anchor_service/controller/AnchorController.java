package com.avisheku.anchor_service.controller;

import com.avisheku.common.record.Anchor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.avisheku.anchor_service.service.AnchorService;
import com.avisheku.common.neo4j.AnchorEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/anchors")
public class AnchorController {

    private final AnchorService anchorService;

    public AnchorController(AnchorService anchorService) {
        this.anchorService = anchorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnchorEntity processAnchor(@RequestBody Anchor anchor) {
        return anchorService.saveAnchor(anchor);
    }

    @GetMapping("/{name}")
    public ResponseEntity<AnchorEntity> getAnchorByName(@PathVariable String name) {
        return anchorService.getAnchorByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<AnchorEntity>> getAllAnchors() {
        List<AnchorEntity> anchors = anchorService.getAllAnchors();
        if (anchors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(anchors);
    }

    @PatchMapping("/update-name")
    public ResponseEntity<AnchorEntity> updateAnchorName(@RequestParam String oldName, @RequestParam String newName) {
        AnchorEntity updatedAnchor = anchorService.updateAnchorNameByOldName(oldName, newName);
        return ResponseEntity.ok(updatedAnchor);
    }
}