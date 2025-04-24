package com.avisheku.anchor_service.repository;

import com.avisheku.common.neo4j.LeafEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface LeafRepository extends Neo4jRepository<LeafEntity, Long> {
    Optional<LeafEntity> findByName(String name);
}