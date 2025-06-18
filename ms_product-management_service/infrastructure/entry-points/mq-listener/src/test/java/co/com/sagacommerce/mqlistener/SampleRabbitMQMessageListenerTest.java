package co.com.sagacommerce.mqlistener;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.mqlistener.fallback.SampleRabbitMQFallbackSender;
import co.com.sagacommerce.usecase.purchasetransaction.CompensationFailedPurchaseUseCase;
import co.com.sagacommerce.usecase.purchasetransaction.PurchaseTransactionUseCase;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SampleRabbitMQMessageListenerTest {

    private Receiver receiver;
    private SampleRabbitMQFallbackSender fallbackSender;
    private PurchaseTransactionUseCase purchaseTransactionUseCase;
    private CompensationFailedPurchaseUseCase compensationFailedPurchaseUseCase;
    private SampleRabbitMQMessageListener listener;

    @BeforeEach
    void setUp() {
        receiver = mock(Receiver.class);
        fallbackSender = mock(SampleRabbitMQFallbackSender.class);
        purchaseTransactionUseCase = mock(PurchaseTransactionUseCase.class);
        compensationFailedPurchaseUseCase = mock(CompensationFailedPurchaseUseCase.class);
        listener = new SampleRabbitMQMessageListener(
                "QUEUE_PURCHASE_ORDER",
                receiver,
                purchaseTransactionUseCase,
                compensationFailedPurchaseUseCase,
                fallbackSender
        );
    }

    @Test
    void shouldProcessRequestState() {
        String json = """
            {
              "state": "REQUEST",
              "clientId": "client-123",
              "items": []
            }
        """;
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        Delivery delivery = new Delivery(null,
                new AMQP.BasicProperties.Builder().correlationId("req-001").build(),
                body
        );

        when(receiver.consumeNoAck("QUEUE_PURCHASE_ORDER"))
                .thenReturn(Flux.just(delivery));
        when(purchaseTransactionUseCase.sendPurchaseOrderEvent(any(PurchaseDTO.class), eq("req-001")))
                .thenReturn(Mono.empty());

        listener.initReceiver();

        verify(purchaseTransactionUseCase, timeout(1000)).sendPurchaseOrderEvent(any(PurchaseDTO.class), eq("req-001"));
        verifyNoInteractions(compensationFailedPurchaseUseCase);
    }

    @Test
    void shouldProcessCancelState() {
        String json = """
            {
              "id": 11,
              "state": "CANCEL",
              "clientId": "client-321",
              "items": []
            }
        """;
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        Delivery delivery = new Delivery(null,
                new AMQP.BasicProperties.Builder().correlationId("can-001").build(),
                body
        );

        when(receiver.consumeNoAck("QUEUE_PURCHASE_ORDER"))
                .thenReturn(Flux.just(delivery));
        when(compensationFailedPurchaseUseCase.sendCanceledOrderEvent(any(PurchaseDTO.class), eq("can-001")))
                .thenReturn(Mono.empty());

        listener.initReceiver();

        verify(compensationFailedPurchaseUseCase, timeout(1000)).sendCanceledOrderEvent(any(PurchaseDTO.class), eq("can-001"));
        verifyNoInteractions(purchaseTransactionUseCase);
    }

    @Test
    void shouldFallbackOnInvalidJson() {
        String invalidJson = "{\"id\": 1, \"state\": \"REQUEST\" "; // mal formado
        byte[] body = invalidJson.getBytes(StandardCharsets.UTF_8);

        Delivery delivery = new Delivery(null,
                new AMQP.BasicProperties.Builder().correlationId("bad-001").build(),
                body
        );

        when(receiver.consumeNoAck("QUEUE_PURCHASE_ORDER"))
                .thenReturn(Flux.just(delivery));
        when(fallbackSender.executeFallbackQueue(eq(delivery), any(Throwable.class), eq("bad-001")))
                .thenReturn(Mono.empty());

        listener.initReceiver();

        verify(fallbackSender, timeout(1000)).executeFallbackQueue(eq(delivery), any(Throwable.class), eq("bad-001"));
        verifyNoInteractions(purchaseTransactionUseCase, compensationFailedPurchaseUseCase);
    }

    @Test
    void shouldFallbackOnUseCaseException() throws InterruptedException {
        String json = """
        {
          "state": "REQUEST",
          "clientId": "client-123"
        }
    """;
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        Delivery delivery = new Delivery(null,
                new AMQP.BasicProperties.Builder().correlationId("fail-001").build(),
                body
        );

        when(receiver.consumeNoAck("QUEUE_PURCHASE_ORDER"))
                .thenReturn(Flux.just(delivery));
        when(purchaseTransactionUseCase.sendPurchaseOrderEvent(any(PurchaseDTO.class), eq("fail-001")))
                .thenReturn(Mono.error(new RuntimeException("Simulated error")));
        when(fallbackSender.executeFallbackQueue(eq(delivery), any(Throwable.class), eq("fail-001")))
                .thenReturn(Mono.empty());

        listener.initReceiver();

        // Esperar a que se procese (máximo 1 segundo)
        Thread.sleep(300); // Puedes ajustar este valor

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(fallbackSender, atLeastOnce()).executeFallbackQueue(eq(delivery), errorCaptor.capture(), eq("fail-001"));
        assertEquals("Simulated error", errorCaptor.getValue().getMessage());
    }

    @Test
    void shouldFallbackOnInvalidState() {
        String json = """
            {
              "id": 88,
              "state": "UNKNOWN",
              "clientId": "client-x"
            }
        """;
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        Delivery delivery = new Delivery(null,
                new AMQP.BasicProperties.Builder().correlationId("stinv-001").build(),
                body
        );

        when(receiver.consumeNoAck("QUEUE_PURCHASE_ORDER"))
                .thenReturn(Flux.just(delivery));
        when(fallbackSender.executeFallbackQueue(eq(delivery), any(Throwable.class), eq("stinv-001")))
                .thenReturn(Mono.empty());

        listener.initReceiver();

        verify(fallbackSender, timeout(1000)).executeFallbackQueue(eq(delivery), any(BusinessException.class), eq("stinv-001"));
        verifyNoInteractions(purchaseTransactionUseCase, compensationFailedPurchaseUseCase);
    }

    @Test
    void shouldIgnoreMessageWithoutCorrelationId() {
        String json = """
            {
              "id": 55,
              "state": "REQUEST"
            }
        """;
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        Delivery delivery = new Delivery(null, new AMQP.BasicProperties(), body);

        when(receiver.consumeNoAck("QUEUE_PURCHASE_ORDER"))
                .thenReturn(Flux.just(delivery));

        listener.initReceiver();

        verifyNoInteractions(purchaseTransactionUseCase, compensationFailedPurchaseUseCase, fallbackSender);
    }

    @Test
    void shouldDisposeOnCleanup() {
        Disposable disposable = mock(Disposable.class);
        when(disposable.isDisposed()).thenReturn(false);

        when(receiver.consumeNoAck(anyString())).thenReturn(Flux.just(mock(Delivery.class)));
        listener.initReceiver();

        listener.cleanup();

        verify(receiver).close();
    }
}