package co.com.sagacommerce.mqlistener;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage;
import co.com.sagacommerce.mqlistener.fallback.SampleRabbitMQFallbackSender;
import co.com.sagacommerce.usecase.purchasetransaction.CompensationFailedPurchaseUseCase;
import co.com.sagacommerce.usecase.purchasetransaction.PurchaseTransactionUseCase;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.rabbitmq.Receiver;

import java.util.Objects;
import java.util.logging.Level;


import static co.com.sagacommerce.model.validation.ValidationService.validate;
import static co.com.sagacommerce.mqlistener.config.JsonUtils.fromJson;


@Log
@Component
public class SampleRabbitMQMessageListener {

    private final PurchaseTransactionUseCase purchaseTransactionUseCase;

    private final CompensationFailedPurchaseUseCase compensationFailedPurchaseUseCase;

    private final SampleRabbitMQFallbackSender sampleRabbitMQFallbackSender;

    private final Receiver receiver;

    private final String inputQueue;


    private Disposable disposable;

    public SampleRabbitMQMessageListener(@Value("${rabbit.mq.input-queue}") String inputQueue,
                                         Receiver receiver, PurchaseTransactionUseCase purchaseTransactionUseCase,
                                         CompensationFailedPurchaseUseCase compensationFailedPurchaseUseCase,
                                         SampleRabbitMQFallbackSender sampleRabbitMQFallbackSender) {
        this.purchaseTransactionUseCase = purchaseTransactionUseCase;
        this.receiver = receiver;
        this.inputQueue = inputQueue;
        this.compensationFailedPurchaseUseCase = compensationFailedPurchaseUseCase;
        this.sampleRabbitMQFallbackSender = sampleRabbitMQFallbackSender;
    }


    @PostConstruct
    public void initReceiver() {
        disposable = receiver.consumeNoAck(inputQueue)
                .doOnNext(m -> log.info("Received message " + new String(m.getBody()) +
                        " with correlationId " + m.getProperties().getCorrelationId()))
                .filter(delivery -> delivery.getProperties().getCorrelationId() != null)
                .flatMap(delivery -> {
                    String correlationId = delivery.getProperties().getCorrelationId();

                    return fromJson(new String(delivery.getBody()), PurchaseDTO.class)
                            .flatMap(purchaseRequest ->
                                    {
                                        validate(purchaseRequest.getState(), Objects::isNull, "Invalid state");

                                        return switch (purchaseRequest.getState()) {
                                            case "REQUEST" -> {
                                                validate(purchaseRequest.getId(), Objects::nonNull, "Invalid id");
                                                yield  purchaseTransactionUseCase
                                                        .sendPurchaseOrderEvent(purchaseRequest, correlationId);
                                            }
                                            case "CANCEL" -> {
                                                validate(purchaseRequest.getId(), Objects::isNull, "Invalid id");
                                                yield compensationFailedPurchaseUseCase
                                                        .sendCanceledOrderEvent(purchaseRequest, correlationId);
                                            }
                                            default -> throw new BusinessException(BusinessErrorMessage.VALIDATION_DATA_ERROR,
                                                    "Invalid state");

                                        };


                                    }
                            )
                            .onErrorResume(error -> {
                                log.log(Level.SEVERE, "Error processing message: " + error.getMessage(), error);
                                return sampleRabbitMQFallbackSender
                                        .executeFallbackQueue(delivery, error, correlationId);
                            });
                })
                .subscribe();
    }

    @PreDestroy
    public void cleanup() {
        receiver.close();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
