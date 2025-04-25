package com.avisheku.rule_service.config;

import com.avisheku.common.model.InternalNotification;
import com.avisheku.common.neo4j.AnchorEntity;
import com.avisheku.common.neo4j.NodeEntity;
import com.avisheku.common.neo4j.LeafEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ConsumerFactory<String, InternalNotification> consumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.avisheku.common.model,com.avisheku.common.neo4j");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, InternalNotification.class);
        props.put(JsonDeserializer.TYPE_MAPPINGS, 
            "anchor:com.avisheku.common.neo4j.AnchorEntity," +
            "node:com.avisheku.common.neo4j.NodeEntity," +
            "leaf:com.avisheku.common.neo4j.LeafEntity");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InternalNotification> kafkaListenerContainerFactory(ConsumerFactory<String, InternalNotification> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, InternalNotification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
} 