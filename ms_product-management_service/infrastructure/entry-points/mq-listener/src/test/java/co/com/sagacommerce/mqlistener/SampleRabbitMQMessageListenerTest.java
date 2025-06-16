package co.com.sagacommerce.mqlistener;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.usecase.purchasetransaction.PurchaseTransactionUseCase;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SampleRabbitMQMessageListenerTest {

    private Receiver receiver;
    private PurchaseTransactionUseCase purchaseTransactionUseCase;
    private SampleRabbitMQMessageListener listener;

    @BeforeEach
    void setUp() {
        receiver = mock(Receiver.class);
        purchaseTransactionUseCase = mock(PurchaseTransactionUseCase.class);
        listener = new SampleRabbitMQMessageListener("QUEUE_PURCHASE_ORDER", receiver, purchaseTransactionUseCase);
    }

    @Test
    void shouldProcessValidMessage() {
        // Arrange
        String json = """
                {
                  "clientId": "client-123",
                  "date": "2025-06-15T10:30:00",
                  "paymentMethod": "CREDIT_CARD",
                  "comment": "Primera compra",
                  "items": []
                }
                """;

        byte[] body = json.getBytes(StandardCharsets.UTF_8);

        Delivery delivery = new Delivery(null,
                new AMQP.BasicProperties.Builder().correlationId("123").build(),
                body
        );

        when(receiver.consumeNoAck("QUEUE_PURCHASE_ORDER"))
                .thenReturn(Flux.just(delivery));

        when(purchaseTransactionUseCase.sendPurchaseOrderEvent(any(PurchaseDTO.class), eq("123")))
                .thenReturn(Mono.empty());

        // Act
        listener.initReceiver();

        // Assert
        verify(purchaseTransactionUseCase, timeout(1000)).sendPurchaseOrderEvent(any(PurchaseDTO.class), eq("123"));
    }

    @Test
    void shouldHandleInvalidJsonGracefully() {
        String invalidJson = "{\"clientId\": \"client-123\",}"; // Mal formado

        byte[] body = invalidJson.getBytes(StandardCharsets.UTF_8);

        Delivery delivery = new Delivery(null,
                new AMQP.BasicProperties.Builder().correlationId("123").build(),
                body
        );

        when(receiver.consumeNoAck("QUEUE_PURCHASE_ORDER"))
                .thenReturn(Flux.just(delivery));

        // Act
        listener.initReceiver();

        // Assert: no llamada al caso de uso porque el JSON falla
        verify(purchaseTransactionUseCase, timeout(500).times(0))
                .sendPurchaseOrderEvent(any(PurchaseDTO.class), anyString());
    }

    @Test
    void shouldDisposeOnCleanup() {
        String json = """
                {
                  "clientId": "client-123",
                  "date": "2025-06-15T10:30:00",
                  "paymentMethod": "CREDIT_CARD",
                  "comment": "Primera compra",
                  "items": []
                }
                """;

        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        Disposable disposable = mock(Disposable.class);
        when(disposable.isDisposed()).thenReturn(false);
        listener = new SampleRabbitMQMessageListener("QUEUE_PURCHASE_ORDER", receiver, purchaseTransactionUseCase);
        Delivery delivery = new Delivery(null,
                new AMQP.BasicProperties.Builder().correlationId("123").build(),
                body
        );

        when(receiver.consumeNoAck("QUEUE_PURCHASE_ORDER"))
                .thenReturn(Flux.just(delivery));
        listener.initReceiver();

        // Simular que disposable fue asignado internamente
        listener.cleanup();

        verify(receiver).close();
    }
}