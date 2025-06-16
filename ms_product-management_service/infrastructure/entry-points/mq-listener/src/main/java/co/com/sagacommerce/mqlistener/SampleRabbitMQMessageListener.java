package co.com.sagacommerce.mqlistener;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.usecase.purchasetransaction.PurchaseTransactionUseCase;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;

import java.util.logging.Level;


import static co.com.sagacommerce.mqlistener.config.JsonUtils.fromJson;


@Log
@Component
public class SampleRabbitMQMessageListener {

    private final PurchaseTransactionUseCase purchaseTransactionUseCase;

    private final Receiver receiver;

    private final String INPUT_QUEUE;

    private Disposable disposable;

    public SampleRabbitMQMessageListener(@Value("${rabbit.mq.input-queue}") String inputQueue, Receiver receiver,
                                         PurchaseTransactionUseCase purchaseTransactionUseCase) {
        this.purchaseTransactionUseCase = purchaseTransactionUseCase;
        this.receiver = receiver;
        INPUT_QUEUE = inputQueue;
    }


    @PostConstruct
    public void initReceiver() {
        disposable = receiver.consumeNoAck(INPUT_QUEUE)
                .doOnNext(m -> log.info("Received message " + new String(m.getBody()) +
                        " with correlationId " + m.getProperties().getCorrelationId()))
                .filter(delivery -> delivery.getProperties().getCorrelationId() != null)
                .flatMap(delivery ->
                        Mono.defer(() ->
                                fromJson(new String(delivery.getBody()), PurchaseDTO.class)
                                        .map(purchaseRequest ->
                                                purchaseTransactionUseCase
                                                        .sendPurchaseOrderEvent(
                                                                purchaseRequest,
                                                                delivery.getProperties().getCorrelationId())
                                                        .subscribe()
                                        )
                                        .onErrorResume(e -> {
                                            log.log(Level.SEVERE, e.getMessage());
                                            return Mono.empty(); // o lógica de DLQ
                                        })
                        )
                ).subscribe();
    }

    @PreDestroy
    public void cleanup() {
        receiver.close();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
