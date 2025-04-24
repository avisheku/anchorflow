package com.avisheku.anchor_service.repository;

import com.avisheku.common.postgresql.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntity, Long> {
}