package com.avisheku.rule_service.repository;

import com.avisheku.common.neo4j.AnchorEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface AnchorRepository extends Neo4jRepository<AnchorEntity, Long> {
    Optional<AnchorEntity> findByName(String name);
}