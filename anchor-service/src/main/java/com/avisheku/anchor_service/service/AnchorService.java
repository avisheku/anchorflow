package com.avisheku.anchor_service.service;

import com.avisheku.anchor_service.repository.AnchorRepository;
import com.avisheku.common.neo4j.*;
import com.avisheku.common.postgresql.ActionItems;
import com.avisheku.common.postgresql.EntityType;
import com.avisheku.common.record.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AnchorService {

    private final AnchorRepository anchorRepository;
    private final LoggerService loggerService;
    private final ObjectMapper objectMapper;

    public AnchorService(AnchorRepository anchorRepository, LoggerService loggerService, ObjectMapper objectMapper) {
        this.anchorRepository = anchorRepository;
        this.loggerService = loggerService;
        this.objectMapper = objectMapper;
    }

    public AnchorEntity updateAnchorNameByOldName(String oldName, String newName) {
        AnchorEntity anchorEntity = anchorRepository.findByName(oldName)
                .orElseThrow(() -> new IllegalArgumentException("AnchorEntity not found with name: " + oldName));
        anchorEntity.setName(newName);
        try {
            loggerService.logAction(ActionItems.AVISHEKU.name(), EntityType.ANCHOR, ActionItems.UPDATE_ANCHOR_NAME.name(), oldName, objectMapper.writeValueAsString(anchorEntity));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize anchor entity", e);
        }
        return anchorRepository.save(anchorEntity);
    }

    public Optional<AnchorEntity> getAnchorByName(String name) {
        return anchorRepository.findByName(name);
    }

    public List<AnchorEntity> getAllAnchors() {
        return anchorRepository.findAll();
    }

    public AnchorEntity saveAnchor(Anchor anchor) {
        List<NodeEntity> nodeEntities = anchor.nodes()
                .map(nodes -> nodes.stream()
                        .map(this::mapToNodeEntity)
                        .toList())
                .orElse(null);

        AnchorEntity entity = AnchorEntity.builder()
                .name(anchor.name())
                .type(anchor.type())
                .displayName(anchor.name())
                .description(anchor.description().orElse(null))
                .nodes(nodeEntities)
                .build();

        try {
            loggerService.logAction(ActionItems.AVISHEKU.name(), EntityType.ANCHOR, ActionItems.CREATE_ANCHOR.name(), anchor.name(), objectMapper.writeValueAsString(anchor));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize anchor", e);
        }
        return anchorRepository.save(entity);
    }

    private NodeEntity mapToNodeEntity(Node node) {
        List<NodeEntity> children = node.nodes()
                .map(childList -> childList.stream()
                        .map(this::mapToNodeEntity)
                        .toList())
                .orElse(null);

        List<LeafEntity> leafs = node.leafs()
                .map(leafList -> leafList.stream()
                        .map(this::mapToLeafEntity)
                        .toList())
                .orElse(null);

        return NodeEntity.builder()
                .name(node.name())
                .type(node.type())
                .displayName(node.name())
                .description(node.description().orElse(null))
                .nodes(children)
                .leafs(leafs)
                .build();
    }

    private LeafEntity mapToLeafEntity(Leaf leaf) {
        return LeafEntity.builder()
                .name(leaf.name())
                .type(leaf.type())
                .displayName(leaf.name())
                .description(leaf.description().orElse(null))
                .build();
    }
}

