package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.custom.storefrontapp.configuration.EmbeddedRedisConfiguration;
import ru.custom.storefrontapp.configuration.NoOAuth2TestConfig;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.repository.CartCacheRepository;
import ru.custom.storefrontapp.repository.ItemCacheRepository;

import java.math.BigDecimal;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles({"test", "redis-test"})
@Import({EmbeddedRedisConfiguration.class, NoOAuth2TestConfig.class})
abstract class BaseCacheRepository {
    @Autowired
    protected CartCacheRepository cartCacheRepository;

    @Autowired
    protected ItemCacheRepository itemCacheRepository;

    @Autowired
    protected ReactiveRedisTemplate<String, Object> redisTemplate;
    @Autowired
    protected ReactiveRedisTemplate<String, ItemDto> itemDtoRedisTemplate;
    protected ReactiveHashOperations<String, Object, Object> hashOps;
    @TempDir
    static Path tempDir;

    protected static final Long TEST_ID = 1L;
    protected static final String TEST_TITLE = "test title";
    protected static final String TEST_DESCRIPTION = "test description";
    protected static final BigDecimal TEST_PRICE = BigDecimal.valueOf(100);
    protected static final String TEST_IMG_PATH = "/test/path.png";
    protected static final Long TEST_USER_ID = 1L;
    protected static final String TEST_FIRST_ID = "1";
    protected static final String TEST_FIRST_COUNT = "2";
    protected static final String TEST_SECOND_ID = "2";
    protected static final String TEST_SECOND_COUNT = "5";

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
        registry.add("spring.data.redis.host", () -> EmbeddedRedisConfiguration.redisContainer.getHost());
        registry.add("spring.data.redis.port", () -> EmbeddedRedisConfiguration.redisContainer.getMappedPort(6379));
    }
}