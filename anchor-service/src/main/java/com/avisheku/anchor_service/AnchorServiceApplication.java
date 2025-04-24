package com.avisheku.anchor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.avisheku.common.postgresql", "com.avisheku.anchor_service"})
@EnableJpaRepositories(basePackages = {"com.avisheku.anchor_service.repository"})
public class AnchorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnchorServiceApplication.class, args);
	}

}
