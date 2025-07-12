package ru.custom.intershop.integration.controller;

import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
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
    protected MockMvc mockMvc;
    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
    }
}
