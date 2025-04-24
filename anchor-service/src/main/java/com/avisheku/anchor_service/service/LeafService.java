package com.avisheku.anchor_service.service;

import com.avisheku.common.neo4j.LeafEntity;
import com.avisheku.anchor_service.repository.LeafRepository;
import com.avisheku.common.postgresql.ActionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeafService {

    private final LeafRepository leafRepository;
    private final LogService logService;

    public LeafService(LeafRepository leafRepository, LogService logService) {
        this.leafRepository = leafRepository;
        this.logService = logService;
    }

    public LeafEntity updateLeafNameByOldName(String oldName, String newName) {
        LeafEntity leafEntity = leafRepository.findByName(oldName)
                .orElseThrow(() -> new IllegalArgumentException("LeafEntity not found with name: " + oldName));
        leafEntity.setName(newName);
        logService.logAction(ActionType.AVISHEKU.name(), ActionType.UPDATE_LEAF_NAME.name(), oldName, leafEntity);
        return leafRepository.save(leafEntity);
    }

    public Optional<LeafEntity> getLeafByName(String name) {
        return leafRepository.findByName(name);
    }

    public List<LeafEntity> getAllLeafs() {
        return leafRepository.findAll();
    }
}