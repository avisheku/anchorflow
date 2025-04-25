package com.avisheku.rule_service.service;

import com.avisheku.common.postgresql.EntityType;
import com.avisheku.common.postgresql.LogEntity;
import com.avisheku.rule_service.repository.LogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class LoggerService {
    private final LogRepository logRepository;

    public LoggerService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void logAction(String userId, EntityType type, String action, String name, String jsonPayload) {
        try {
            LogEntity logEntity = new LogEntity();
            logEntity.setRequestTimestamp(LocalDateTime.now());
            logEntity.setUserId(userId);
            logEntity.setType(type.name());
            logEntity.setAction(action);
            logEntity.setName(name);
            logEntity.setEntityJson(jsonPayload);
            logRepository.save(logEntity);
        } catch (Exception e) {
            log.error("Failed to log action", e);
        }
    }
} 