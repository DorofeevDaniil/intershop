package ru.custom.storefrontapp.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import jakarta.annotation.PreDestroy;

@TestConfiguration
@Profile("redis-test")
public class EmbeddedRedisConfiguration {

    private RedisServer redisServer;

    @Bean(destroyMethod = "stop")
    public RedisServer redisServer() {
        int redisPort = Integer.parseInt(System.getProperty("spring.data.redis.port", "6379"));
        redisServer = RedisServer.builder()
            .port(redisPort)
            .setting("maxmemory 128M")
            .build();
        redisServer.start();
        return redisServer;
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}