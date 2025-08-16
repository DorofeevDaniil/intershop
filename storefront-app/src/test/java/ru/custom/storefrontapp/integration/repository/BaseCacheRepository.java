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
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.repository.CartCacheRepository;
import ru.custom.storefrontapp.repository.ItemCacheRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

@SpringBootTest
@ActiveProfiles({"test", "redis-test"})
@Import(EmbeddedRedisConfiguration.class)
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

    protected static final String TEST_FIRST_ID = "1";
    protected static final String TEST_FIRST_COUNT = "2";
    protected static final String TEST_SECOND_ID = "2";
    protected static final String TEST_SECOND_COUNT = "5";

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
        int redisPort = findAvailableTcpPort();
        System.setProperty("spring.data.redis.port", String.valueOf(redisPort));
        registry.add("spring.data.redis.port", () -> redisPort);
        registry.add("spring.data.redis.host", () -> "localhost");
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