package com.avisheku.anchor_service.service;

import com.avisheku.anchor_service.repository.NodeRepository;
import com.avisheku.common.neo4j.NodeEntity;
import com.avisheku.common.postgresql.ActionItems;
import com.avisheku.common.postgresql.EntityType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NodeService {

    private final NodeRepository nodeRepository;
    private final LoggerService loggerService;
    private final ObjectMapper objectMapper;

    public NodeService(NodeRepository nodeRepository, LoggerService loggerService, ObjectMapper objectMapper) {
        this.nodeRepository = nodeRepository;
        this.loggerService = loggerService;
        this.objectMapper = objectMapper;
    }

    public NodeEntity updateNodeNameByOldName(String oldName, String newName) {
        NodeEntity nodeEntity = nodeRepository.findByName(oldName)
                .orElseThrow(() -> new IllegalArgumentException("NodeEntity not found with name: " + oldName));
        nodeEntity.setName(newName);
        try {
            loggerService.logAction(ActionItems.AVISHEKU.name(), EntityType.NODE, ActionItems.UPDATE_NODE_NAME.name(), oldName, objectMapper.writeValueAsString(nodeEntity));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize node entity", e);
        }
        return nodeRepository.save(nodeEntity);
    }

    public Optional<NodeEntity> getNodeByName(String name) {
        return nodeRepository.findByName(name);
    }

    public List<NodeEntity> getAllNodes() {
        return nodeRepository.findAll();
    }
}