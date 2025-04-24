package com.avisheku.anchor_service.controller;

import com.avisheku.common.neo4j.LeafEntity;
import com.avisheku.anchor_service.service.LeafService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leafs")
public class LeafController {

    private final LeafService leafService;

    public LeafController(LeafService leafService) {
        this.leafService = leafService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<LeafEntity> getLeafByName(@PathVariable String name) {
        return leafService.getLeafByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<LeafEntity>> getAllLeafs() {
        List<LeafEntity> leafs = leafService.getAllLeafs();
        if (leafs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(leafs);
    }

    @PatchMapping("/update-name")
    public ResponseEntity<LeafEntity> updateLeafName(@RequestParam String oldName, @RequestParam String newName) {
        LeafEntity updatedLeaf = leafService.updateLeafNameByOldName(oldName, newName);
        return ResponseEntity.ok(updatedLeaf);
    }
}