package com.avisheku.anchor_service.service;

import com.avisheku.anchor_service.repository.AnchorRepository;
import com.avisheku.common.neo4j.*;
import com.avisheku.common.postgresql.ActionType;
import com.avisheku.common.record.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AnchorService {

    private final AnchorRepository anchorRepository;
    private final LogService logService;

    public AnchorService(AnchorRepository anchorRepository, LogService logService) {
        this.anchorRepository = anchorRepository;
        this.logService = logService;
    }

    public AnchorEntity updateAnchorNameByOldName(String oldName, String newName) {
        AnchorEntity anchorEntity = anchorRepository.findByName(oldName)
                .orElseThrow(() -> new IllegalArgumentException("AnchorEntity not found with name: " + oldName));
        anchorEntity.setName(newName);
        logService.logAction(ActionType.AVISHEKU.name(), ActionType.UPDATE_ANCHOR_NAME.name(), oldName, anchorEntity);
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

        logService.logAction(ActionType.AVISHEKU.name(), ActionType.CREATE_ANCHOR.name(), anchor.name(), anchor);
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

