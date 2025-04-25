package com.avisheku.anchor_service.service;

import com.avisheku.anchor_service.repository.LogRepository;
import com.avisheku.common.model.InternalNotification;
import com.avisheku.common.postgresql.EntityType;
import com.avisheku.common.postgresql.LogEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LoggerService {
    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public LoggerService(LogRepository logRepository, ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.logRepository = logRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
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

            publishToKafka(type, action, jsonPayload);

        } catch (Exception e) {
            throw new RuntimeException("Failed to log action", e);
        }
    }

    private void publishToKafka(EntityType type, String action, String jsonPayload) throws JsonProcessingException {
        InternalNotification notification = new InternalNotification(
            UUID.randomUUID().toString(),
            type.name(),
            action,
            jsonPayload,
            System.currentTimeMillis()
        );
        
        kafkaTemplate.send("internal-notification", objectMapper.writeValueAsString(notification));
    }
} 