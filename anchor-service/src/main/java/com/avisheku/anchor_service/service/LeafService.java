package com.avisheku.anchor_service.service;

import com.avisheku.common.neo4j.LeafEntity;
import com.avisheku.anchor_service.repository.LeafRepository;
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
public class LeafService {

    private final LeafRepository leafRepository;
    private final LoggerService loggerService;
    private final ObjectMapper objectMapper;

    public LeafService(LeafRepository leafRepository, LoggerService loggerService, ObjectMapper objectMapper) {
        this.leafRepository = leafRepository;
        this.loggerService = loggerService;
        this.objectMapper = objectMapper;
    }

    public LeafEntity updateLeafNameByOldName(String oldName, String newName) {
        LeafEntity leafEntity = leafRepository.findByName(oldName)
                .orElseThrow(() -> new IllegalArgumentException("LeafEntity not found with name: " + oldName));
        leafEntity.setName(newName);
        try {
            loggerService.logAction(ActionItems.AVISHEKU.name(), EntityType.LEAF, ActionItems.UPDATE_LEAF_NAME.name(), oldName, objectMapper.writeValueAsString(leafEntity));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize leaf entity", e);
        }
        return leafRepository.save(leafEntity);
    }

    public Optional<LeafEntity> getLeafByName(String name) {
        return leafRepository.findByName(name);
    }

    public List<LeafEntity> getAllLeafs() {
        return leafRepository.findAll();
    }
}