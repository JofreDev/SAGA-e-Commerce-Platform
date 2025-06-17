package co.com.sagacommerce.mqlistener.fallback;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import static co.com.sagacommerce.mqlistener.config.JsonUtils.OBJECT_MAPPER;


@Log
@Component
public class SampleRabbitMQFallbackSender {

    private final String outputErrorQueue;
    private final Sender sender;

    public SampleRabbitMQFallbackSender(@Value("${rabbit.mq.output-error-queue}") String outputErrorQueue,
                                        @Qualifier("errorSender") Sender sender) {
        this.sender = sender;
        this.outputErrorQueue = outputErrorQueue;
    }


    public Mono<Void> executeFallbackQueue(Delivery delivery, Throwable error, String correlationId) {

        Mono<OutboundMessage> outbound = Mono.fromCallable(() ->
                new OutboundMessage(
                        "",
                        outputErrorQueue,
                        new AMQP.BasicProperties.Builder()
                                .correlationId(correlationId)
                                .build(),
                        fromDeliveryErrorToBytes(delivery, error, correlationId)
                )
        );

        return sender.declareQueue(QueueSpecification.queue(outputErrorQueue))
                .then(sender.send(outbound))
                .doOnError(e -> log.log(Level.SEVERE, "Send failed", e));
    }

    private static byte[] fromDeliveryErrorToBytes(Delivery delivery, Throwable error, String correlationId) {
        try {
            ObjectNode originalNode = OBJECT_MAPPER.readValue(delivery.getBody(), ObjectNode.class);

            originalNode.put("correlationId", correlationId != null ? correlationId : "N/A");
            originalNode.put("error", error.getMessage());

            return OBJECT_MAPPER.writeValueAsBytes(originalNode);
        } catch (Exception e) {
            log.severe("Error al serializar el mensaje de error: " + e.getMessage());
            return ("{\"error\":\"" + error.getMessage() + "\"}").getBytes(StandardCharsets.UTF_8);
        }
    }

    @PreDestroy
    public void cleanup() {
        sender.close();
    }
}
