package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import ru.custom.storefrontapp.configuration.EmbeddedRedisConfiguration;
import ru.custom.storefrontapp.configuration.NoOAuth2TestConfig;
import ru.custom.storefrontapp.repository.*;

import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles({"test", "redis-test"})
@Import({EmbeddedRedisConfiguration.class, NoOAuth2TestConfig.class})
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
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserRoleRepository userRoleRepository;

    protected static final Integer TEST_PAGE = 1;
    protected static final Integer TEST_PAGE_SIZE = 10;
    protected static final String SEARCH_PARAM = "1";
    protected static final Long TEST_USER_ID = 1L;
    protected static final String TEST_USER_NAME = "user";
    protected static final String TEST_ROLE_NAME = "USER";
    protected static final Long TEST_ROLE_ID = 1L;
    @TempDir
    static Path tempDir;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
        registry.add("spring.data.redis.host", () -> EmbeddedRedisConfiguration.redisContainer.getHost());
        registry.add("spring.data.redis.port", () -> EmbeddedRedisConfiguration.redisContainer.getMappedPort(6379));
    }
}
