package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import ru.custom.storefrontapp.repository.ItemRepository;
import ru.custom.storefrontapp.repository.OrderItemRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles("test")
@Sql(
    scripts = "/sql/reset-db.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@Sql(
    scripts = "/sql/seed-db.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
abstract class BaseRepositoryTest {
    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected OrderItemRepository orderItemRepository;
    protected static final Integer TEST_PAGE = 1;
    protected static final Integer TEST_PAGE_SIZE = 10;
    protected static final String SEARCH_PARAM = "1";
    @TempDir
    static Path tempDir;
    private static int redisPort;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
        redisPort = findAvailableTcpPort();
        registry.add("spring.data.redis.port", () -> redisPort);
        registry.add("spring.data.redis.host", () -> "localhost");
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
