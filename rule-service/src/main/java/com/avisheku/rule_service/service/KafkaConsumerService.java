package com.avisheku.rule_service.service;

import com.avisheku.common.model.InternalNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    @KafkaListener(topics = "${kafka.topic.internal-notification}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenInternalNotification(InternalNotification notification) {
        log.info("Received internal notification: type={}, action={}, payload={}", 
            notification.type(), 
            notification.action(), 
            notification.payload());
    }
} 