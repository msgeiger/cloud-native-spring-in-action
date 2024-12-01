package com.polarbookshop.edgeservice;

import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestEnvironmentConfiguration {

    private static final int REDIS_PORT = 6379;

    @Bean
    GenericContainer<?> genericContainer() {
        return new GenericContainer<>(DockerImageName.parse("7.4-rc-alpine3.20"))
            .withExposedPorts(REDIS_PORT);
    }
}
