package ru.custom.intershop.integration.controller;

import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.file.Path;

@ActiveProfiles("test")
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

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
    }
}
