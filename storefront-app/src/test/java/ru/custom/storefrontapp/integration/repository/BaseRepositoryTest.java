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
import ru.custom.storefrontapp.repository.OrderRepository;

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
    protected OrderRepository orderRepository;
    @Autowired
    protected OrderItemRepository orderItemRepository;
    protected static final Integer TEST_PAGE = 1;
    protected static final Integer TEST_PAGE_SIZE = 10;
    protected static final String SEARCH_PARAM = "1";
    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
    }
}
