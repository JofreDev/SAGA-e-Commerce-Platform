package co.com.sagacommerce.mqsender;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import com.rabbitmq.client.AMQP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.timeout;

class SampleRabbitMQMessageSenderTest {

    private String outputQueue;

    private Sender sender;


    private SampleRabbitMQMessageSender sampleRabbitMQMessageSender;

    @BeforeEach
    void setUp() {
        sender = mock(Sender.class);
        outputQueue = "OUT_EXAMPLE_ORDER.QUEUE.RES";
        sampleRabbitMQMessageSender = new SampleRabbitMQMessageSender(outputQueue,sender);
    }

    @Test
    void sendPurchaseOrder() {

        AMQP.Queue.DeclareOk declareOkMock = mock(AMQP.Queue.DeclareOk.class);

        when(sender.declareQueue(any(QueueSpecification.class)))
                .thenReturn(Mono.just(declareOkMock));

        when(sender.send(ArgumentMatchers.<Mono<OutboundMessage>>any()))
                .thenReturn(Mono.empty());

        var purchaseDTO = PurchaseDTO.builder()
                .id(1)
                .clientId("client-123")
                .date(LocalDateTime.now())
                .paymentMethod("CREDIT_CARD")
                .comment("Primera compra de prueba")
                .state("PENDING")
                .items(List.of(
                        new PurchaseItemDTO(101, 2,3, 199.99, 4),
                        new PurchaseItemDTO(102, 1,3, 99.99,5)
                ))
                .build();



        // Act
        sampleRabbitMQMessageSender.sendPurchaseOrder(purchaseDTO, "123").block();

        // Assert
        ArgumentCaptor<Mono<OutboundMessage>> captor = ArgumentCaptor.forClass(Mono.class);
        verify(sender).send(captor.capture());

        // Verificar que el contenido del Mono es el correcto
        Mono<OutboundMessage> capturedMono = captor.getValue();
        assertNotNull(capturedMono);

        StepVerifier.create(capturedMono)
                .assertNext(message -> {
                    assertEquals(outputQueue, message.getRoutingKey());
                    assertEquals("123", message.getProperties().getCorrelationId());
                    assertNotNull(message.getBody());
                    assertTrue(message.getBody().length > 0);
                })
                .verifyComplete();

    }

    @Test
    void cleanup() {

        sampleRabbitMQMessageSender.cleanup();
        verify(sender, timeout(500)).close();

    }
}