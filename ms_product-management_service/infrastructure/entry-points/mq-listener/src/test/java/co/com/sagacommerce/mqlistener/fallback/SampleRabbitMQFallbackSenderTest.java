package co.com.sagacommerce.mqlistener.fallback;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SampleRabbitMQFallbackSenderTest {

    private static final String ERROR_QUEUE = "error.queue";

    private Sender sender;
    private SampleRabbitMQFallbackSender fallbackSender;

    @BeforeEach
    void setUp() {
        sender = mock(Sender.class);

        when(sender.declareQueue(any(QueueSpecification.class))).thenReturn(Mono.empty());
        when(sender.send(any(Mono.class))).thenReturn(Mono.empty());

        fallbackSender = new SampleRabbitMQFallbackSender(ERROR_QUEUE, sender);
    }

    @Test
    void testExecuteFallbackQueue_sendsModifiedMessageToErrorQueue() {
        // given
        String originalJson = "{\"purchaseId\": 123}";

        Delivery delivery = mock(Delivery.class);
        when(delivery.getBody()).thenReturn(originalJson.getBytes(StandardCharsets.UTF_8));
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .correlationId("corr-123")
                .build();
        when(delivery.getProperties()).thenReturn(properties);
        Throwable error = new RuntimeException("Simulated failure");

        // when
        Mono<Void> result = fallbackSender.executeFallbackQueue(delivery, error, "corr-123");

        // then
        StepVerifier.create(result)
                .verifyComplete();

        verify(sender).declareQueue(any(QueueSpecification.class));
        ArgumentCaptor<Mono<OutboundMessage>> captor = ArgumentCaptor.forClass(Mono.class);
        verify(sender).send(captor.capture());

        OutboundMessage message = captor.getValue().block();
        assertNotNull(message);
        assertEquals(ERROR_QUEUE, message.getRoutingKey());
        assertNotNull(message.getProperties());
        assertEquals("corr-123", message.getProperties().getCorrelationId());

        String bodyAsString = new String(message.getBody(), StandardCharsets.UTF_8);
        assertTrue(bodyAsString.contains("\"purchaseId\":123"));
        assertTrue(bodyAsString.contains("\"error\":\"Simulated failure\""));
        assertTrue(bodyAsString.contains("\"correlationId\":\"corr-123\""));
    }

    @Test
    void testExecuteFallbackQueue_whenSerializationFails_shouldFallbackToSimpleErrorJson() {
        // Simulamos un body corrupto que cause fallo en ObjectMapper
        Delivery delivery = mock(Delivery.class);
        when(delivery.getBody()).thenReturn("INVALID_JSON".getBytes(StandardCharsets.UTF_8));
        when(delivery.getProperties()).thenReturn(new AMQP.BasicProperties.Builder()
                .correlationId("bad-correlation")
                .build());

        Throwable error = new RuntimeException("Simulated deep failure");

        Mono<Void> result = fallbackSender.executeFallbackQueue(delivery, error, "bad-correlation");

        StepVerifier.create(result)
                .verifyComplete();

        ArgumentCaptor<Mono<OutboundMessage>> captor = ArgumentCaptor.forClass(Mono.class);
        verify(sender).send(captor.capture());
        OutboundMessage msg = captor.getValue().block();

        assertNotNull(msg);
        String body = new String(msg.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Simulated deep failure"));
        assertTrue(body.contains("\"error\""));
        // En este caso, no hay purchaseId ni otros campos originales, solo el error.
    }

    @Test
    void testCleanup_closesSender() {
        fallbackSender.cleanup();
        verify(sender).close();
    }

}