package com.avisheku.anchor_service.service;

import com.avisheku.anchor_service.repository.NodeRepository;
import com.avisheku.common.neo4j.NodeEntity;
import com.avisheku.common.postgresql.ActionType;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NodeService {

    private final NodeRepository nodeRepository;
    private final LogService logService;

    public NodeService(NodeRepository nodeRepository, LogService logService) {
        this.nodeRepository = nodeRepository;
        this.logService = logService;
    }

    public NodeEntity updateNodeNameByOldName(String oldName, String newName) {
        NodeEntity nodeEntity = nodeRepository.findByName(oldName)
                .orElseThrow(() -> new IllegalArgumentException("NodeEntity not found with name: " + oldName));
        nodeEntity.setName(newName);
        logService.logAction(ActionType.AVISHEKU.name(), ActionType.UPDATE_NODE_NAME.name(), oldName, nodeEntity);
        return nodeRepository.save(nodeEntity);
    }

    public Optional<NodeEntity> getNodeByName(String name) {
        return nodeRepository.findByName(name);
    }

    public List<NodeEntity> getAllNodes() {
        return nodeRepository.findAll();
    }
}