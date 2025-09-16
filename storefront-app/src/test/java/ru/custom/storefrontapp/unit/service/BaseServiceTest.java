package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.model.Item;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
abstract class BaseServiceTest {
    protected static final Long TEST_ID = 1L;
    protected static final String TEST_TITLE = "test title";
    protected static final String TEST_DESCRIPTION = "test description";
    protected static final BigDecimal TEST_PRICE = BigDecimal.valueOf(100);
    protected static final String TEST_IMG_PATH = "/test/path.png";
    protected static final String TEST_PLUS_ACTION = "PLUS";
    protected static final String TEST_MINUS_ACTION = "MINUS";
    protected static final String TEST_DELETE_ACTION = "DELETE";
    protected static final String TEST_FILE_NAME = "test-image.jpg";
    protected static final String TEST_FILE_CONTENT = "test file content";
    protected static final Integer TEST_PAGE = 1;
    protected static final Integer TEST_PAGE_SIZE = 10;
    protected static final String TEST_SORT_ALPHA = "ALPHA";
    protected static final String TEST_SORT_PRICE = "PRICE";
    protected static final String TEST_SORT_NO = "NO";
    protected static final String TEST_SEARCH = "test";
    protected static final Integer TEST_COUNT = 1;
    protected static final Long TEST_USER_ID = 1L;
    protected static final String TEST_USER_NAME = "user";

    protected Item populateItemNoId() {
        Item item = new Item();

        item.setTitle(TEST_TITLE);
        item.setDescription(TEST_DESCRIPTION);
        item.setCount(1);
        item.setPrice(TEST_PRICE);
        item.setImgPath(TEST_IMG_PATH);

        return item;
    }

    protected FilePart populateFilePart() {
        FilePart filePart = mock(FilePart.class);

        lenient().when(filePart.filename()).thenReturn(TEST_FILE_NAME);

        DataBuffer buffer = new DefaultDataBufferFactory().wrap(TEST_FILE_CONTENT.getBytes());
        Flux<DataBuffer> content = Flux.just(buffer);
        lenient().when(filePart.content()).thenReturn(content);

        lenient().when(filePart.transferTo(any(Path.class))).thenAnswer(invocation -> {
            Path path = invocation.getArgument(0);
            return Mono.fromRunnable(() -> {
                try {
                    Files.writeString(path, TEST_FILE_CONTENT);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        return filePart;
    }
}
