package com.avisheku.anchor_service.controller;

import com.avisheku.common.neo4j.NodeEntity;
import com.avisheku.anchor_service.service.NodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nodes")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<NodeEntity> getNodeByName(@PathVariable String name) {
        return nodeService.getNodeByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<NodeEntity>> getAllNodes() {
        List<NodeEntity> nodes = nodeService.getAllNodes();
        if (nodes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nodes);
    }

    @PatchMapping("/update-name")
    public ResponseEntity<NodeEntity> updateNodeName(@RequestParam String oldName, @RequestParam String newName) {
        NodeEntity updatedNode = nodeService.updateNodeNameByOldName(oldName, newName);
        return ResponseEntity.ok(updatedNode);
    }
}
