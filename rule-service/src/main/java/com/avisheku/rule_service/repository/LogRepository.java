package com.avisheku.rule_service.repository;

import com.avisheku.common.postgresql.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface LogRepository extends JpaRepository<LogEntity, Long> {
} 