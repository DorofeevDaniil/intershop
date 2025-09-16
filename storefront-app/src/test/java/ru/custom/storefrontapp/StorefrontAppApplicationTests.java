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

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles({"test", "redis-test"})
@Import({EmbeddedRedisConfiguration.class, NoOAuth2TestConfig.class})
class StorefrontAppApplicationTests {
    @TempDir
    static Path tempDir;
    private static int redisPort;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
        redisPort = findAvailableTcpPort();
        registry.add("spring.data.redis.host", () -> EmbeddedRedisConfiguration.redisContainer.getHost());
        registry.add("spring.data.redis.port", () -> EmbeddedRedisConfiguration.redisContainer.getMappedPort(6379));
    }
    @Test
    void contextLoads() {
        //empty one
    }

    private static int findAvailableTcpPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Could not find available TCP port", e);
        }
    }

}
