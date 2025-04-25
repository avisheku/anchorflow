package com.avisheku.rule_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.avisheku.common.postgresql", "com.avisheku.rule_service"})
@EnableJpaRepositories(basePackages = {"com.avisheku.rule_service.repository"})
public class RuleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RuleServiceApplication.class, args);
	}

}
