package ru.custom.intershop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Path;

@ActiveProfiles("test")
@SpringBootTest
class IntershopApplicationTests {

	@TempDir
	static Path tempDir;

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("app.upload-dir", () -> tempDir.toString());
	}

	@Test
	void contextLoads() {
	}

}
