package com.avisheku.rule_service.repository;

import com.avisheku.common.neo4j.NodeEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface NodeRepository extends Neo4jRepository<NodeEntity, Long> {
    Optional<NodeEntity> findByName(String name);
}