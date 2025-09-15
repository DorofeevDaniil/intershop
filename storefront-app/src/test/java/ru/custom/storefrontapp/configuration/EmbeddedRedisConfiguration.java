package ru.custom.storefrontapp.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@Profile("redis-test")
public class EmbeddedRedisConfiguration {

    public static GenericContainer<?> redisContainer =
            new GenericContainer<>(DockerImageName.parse("redis:7.2.0-alpine"))
                    .withExposedPorts(6379);

    static {
        redisContainer.start();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        String host = redisContainer.getHost();
        Integer port = redisContainer.getMappedPort(6379);
        return new LettuceConnectionFactory(host, port);
    }
}