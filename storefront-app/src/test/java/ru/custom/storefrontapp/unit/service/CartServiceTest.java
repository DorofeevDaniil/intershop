package ru.custom.storefrontapp.unit.service;//package ru.custom.intershop.unit.service;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.bean.override.mockito.MockReset;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//import ru.custom.intershop.model.Cart;
//import ru.custom.intershop.model.Item;
//import ru.custom.intershop.service.CartService;
//import ru.custom.intershop.service.ItemService;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest(classes = CartService.class)
//class CartServiceTest extends BaseServiceTest {
//    @MockitoBean(reset = MockReset.BEFORE)
//    private ItemService itemService;
//
//    @Autowired
//    private CartService cartService;
//
//    private static final String TEST_ACTION = "PLUS";
//
//    @Test
//    void changeItemAmount_shouldChangeAmount() {
//        Item item = populateItem();
//        item.setCount(item.getCount() + 1);
//
//        doReturn(Mono.just(item)).when(itemService).changeAmount(anyLong(), anyString());
//
//        StepVerifier.create(cartService.changeItemAmount(TEST_ID, TEST_ACTION))
//            .assertNext(updatedItem -> assertEquals(updatedItem, item))
//            .verifyComplete();
//
//        verify(itemService, times(1)).changeAmount(TEST_ID, TEST_ACTION);
//    }
//
//    @Test
//    void getCart_shouldReturnCart() {
//        List<Item> items = List.of(populateItem());
//
//        Cart expectedCart = new Cart(items, TEST_PRICE);
//
//        doReturn(Flux.fromIterable(items)).when(itemService).getAllInCart();
//
//        StepVerifier.create(cartService.getCart())
//            .assertNext(actualCart -> assertEquals(actualCart, expectedCart))
//            .verifyComplete();
//    }
//
//    private Item populateItem() {
//        Item item = populateItemNoId();
//
//        item.setId(TEST_ID);
//
//        return item;
//    }
//}
