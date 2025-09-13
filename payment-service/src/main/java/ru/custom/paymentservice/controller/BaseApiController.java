package ru.custom.paymentservice.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.custom.paymentservice.api.DefaultApi;
import ru.custom.paymentservice.domain.BalanceDto;
import ru.custom.paymentservice.domain.PaymentDto;
import ru.custom.paymentservice.service.PaymentService;

@Controller
@RequestMapping("${openapi.paymentService.base-path:}")
public class BaseApiController implements DefaultApi {
    private final PaymentService paymentService;

    public BaseApiController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Mono<ResponseEntity<BalanceDto>> apiBalanceUserIdGet(
            Long userId,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
        Mono<Void> result = Mono.empty();
        exchange.getResponse().setStatusCode(HttpStatus.valueOf(200));
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                return paymentService.getBalance()
                    .map(ResponseEntity::ok);
            }
        }
        return result.then(Mono.empty());

    }

    @Override
    public Mono<ResponseEntity<Void>> apiPaymentPost(
        @Parameter(name = "PaymentDto", description = "", required = true) @Valid @RequestBody Mono<PaymentDto> paymentDto,
        @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
        return paymentService.processPayment(paymentDto)
            .flatMap(isValid ->
                Boolean.TRUE.equals(isValid)
                    ? Mono.just(ResponseEntity.ok().build())
                    : Mono.just(ResponseEntity.badRequest().build()));

    }
}
