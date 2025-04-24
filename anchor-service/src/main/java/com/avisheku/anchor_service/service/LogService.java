package com.avisheku.anchor_service.service;

import com.avisheku.anchor_service.repository.LogRepository;
import com.avisheku.common.postgresql.LogEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogService {
    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;

    public LogService(LogRepository logRepository, ObjectMapper objectMapper) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
    }

    public void logAction(String userId, String action, String name, Object request) {
        try {
            LogEntity logEntity = new LogEntity();
            logEntity.setRequestTimestamp(LocalDateTime.now());
            logEntity.setUserId(userId);
            logEntity.setAction(action);
            logEntity.setName(name);
            logEntity.setEntityJson(objectMapper.writeValueAsString(request));
            logRepository.save(logEntity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to log action", e);
        }
    }
}