package ru.custom.storefrontapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.custom.storefrontapp.configuration.EmbeddedRedisConfiguration;
import ru.custom.storefrontapp.configuration.NoOAuth2TestConfig;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles({"test", "redis-test"})
@Import({EmbeddedRedisConfiguration.class, NoOAuth2TestConfig.class})
class StorefrontAppApplicationTests {
    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
        registry.add("spring.data.redis.host", () -> EmbeddedRedisConfiguration.redisContainer.getHost());
        registry.add("spring.data.redis.port", () -> EmbeddedRedisConfiguration.redisContainer.getMappedPort(6379));
    }
    @Test
    void contextLoads() {
        //empty one
    }

}
