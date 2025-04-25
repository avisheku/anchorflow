package com.avisheku.anchor_service.service;

import com.avisheku.common.neo4j.LeafEntity;
import com.avisheku.anchor_service.repository.LeafRepository;
import com.avisheku.common.postgresql.ActionItems;
import com.avisheku.common.postgresql.EntityType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeafService {

    private final LeafRepository leafRepository;
    private final LoggerService loggerService;

    public LeafService(LeafRepository leafRepository, LoggerService loggerService) {
        this.leafRepository = leafRepository;
        this.loggerService = loggerService;
    }

    public LeafEntity updateLeafNameByOldName(String oldName, String newName) {
        LeafEntity leafEntity = leafRepository.findByName(oldName)
                .orElseThrow(() -> new IllegalArgumentException("LeafEntity not found with name: " + oldName));
        leafEntity.setName(newName);
        loggerService.logAction(ActionItems.AVISHEKU.name(), EntityType.LEAF, ActionItems.UPDATE_LEAF_NAME.name(), oldName, leafEntity);
        return leafRepository.save(leafEntity);
    }

    public Optional<LeafEntity> getLeafByName(String name) {
        return leafRepository.findByName(name);
    }

    public List<LeafEntity> getAllLeafs() {
        return leafRepository.findAll();
    }
}