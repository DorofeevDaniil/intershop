package ru.custom.paymentservice.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.custom.paymentservice.domain.BalanceDto;
import ru.custom.paymentservice.domain.PaymentDto;

@Service
public class PaymentService {
    public Mono<BalanceDto> getBalance() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setBalance(100000.0);

        return Mono.just(balanceDto);
    }

    public Mono<Boolean> processPayment(Mono<PaymentDto> paymentDtoMono) {
        return paymentDtoMono
            .flatMap(paymentDto -> getBalance()
                    .flatMap(balanceDto -> {
                        var paymentDiff = balanceDto.getBalance().compareTo(paymentDto.getAmount());
                        return Mono.just(paymentDiff >= 0);
                    })
            );
    }
}
