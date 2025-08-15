package ru.custom.storefrontapp.unit.initializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.initializer.ImageStartupInitializer;
import ru.custom.storefrontapp.model.Item;
import ru.custom.storefrontapp.service.ItemService;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageStartupInitializerTest {

    @Mock
    private ItemService itemService;

    @Spy
    @InjectMocks
    private ImageStartupInitializer initializer;

    @BeforeEach
    void setUp() throws IOException {
        ReflectionTestUtils.setField(initializer, "relativePath", "uploads/images");

        lenient().doReturn("mock-image.jpg").when(initializer).saveImage(anyString());
        lenient().doReturn("mock text").when(initializer).getFileText(anyString());
    }

    @Test
    void shouldSaveItemsWhenDatabaseIsEmpty() {
        // given
        when(itemService.getTotalCount()).thenReturn(Mono.just(0L));
        when(itemService.saveToDb(any(Item.class))).thenReturn(Mono.just(new Item()));

        // when
        StepVerifier.create(initializer.runItemInitialization())
            .verifyComplete();

        // then
        verify(itemService, atLeastOnce()).saveToDb(any(Item.class));
    }

    @Test
    void shouldDoNothingWhenDatabaseIsNotEmpty() {
        // given
        when(itemService.getTotalCount()).thenReturn(Mono.just(10L));

        // when
        StepVerifier.create(initializer.runItemInitialization())
            .verifyComplete();

        // then
        verify(itemService, never()).saveToDb(any(Item.class));
    }
}
