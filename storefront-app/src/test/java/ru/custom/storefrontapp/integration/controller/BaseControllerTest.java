package ru.custom.storefrontapp.integration.controller;

import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.custom.storefrontapp.configuration.EmbeddedRedisConfiguration;
import ru.custom.storefrontapp.configuration.NoOAuth2TestConfig;

import java.io.IOException;
import java.nio.file.Path;

@ActiveProfiles({"test", "redis-test"})
@Import({EmbeddedRedisConfiguration.class, NoOAuth2TestConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Sql(
    scripts = "/sql/reset-db.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
    scripts = "/sql/seed-db.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
abstract class BaseControllerTest {
    @Autowired
    protected WebTestClient webTestClient;
    @TempDir
    static Path tempDir;
    protected static final String TEST_FILE_NAME = "test-image.jpg";
    protected static final String TEST_FILE_CONTENT = "test file content";
    protected static final MediaType TEST_RESPONSE_MEDIA_TYPE = MediaType.TEXT_HTML;
    protected static final MediaType TEST_FILE_CONTENT_TYPE = MediaType.MULTIPART_FORM_DATA;
    protected static final String TEST_USER_ROLE = "USER";
    protected static final String TEST_ADMIN_ROLE = "ADMIN";

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
        int redisPort = findAvailableTcpPort();
        System.setProperty("spring.data.redis.port", String.valueOf(redisPort));
        registry.add("spring.data.redis.host", () -> EmbeddedRedisConfiguration.redisContainer.getHost());
        registry.add("spring.data.redis.port", () -> EmbeddedRedisConfiguration.redisContainer.getMappedPort(6379));
    }

    private static int findAvailableTcpPort() {
        try (var socket = new java.net.ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Could not find available TCP port", e);
        }
    }
}
