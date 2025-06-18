package com.rabbitMQ.mock.DrivenAdapters.rabbitMQ.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

import java.time.Duration;
import java.util.UUID;

import static com.rabbitMQ.mock.DrivenAdapters.rabbitMQ.consumer.JsonUtils.fromJson;

@Log
@Component
public class SampleRabbitMQMessageSender {

    private final String outputQueue;
    private final String replyQueue;
    private final Sender sender;
    private final Receiver receiver;

    public SampleRabbitMQMessageSender(
            @Value("${rabbit.mq.output-queue}") String outputQueue,
            @Value("${rabbit.mq.reply-queue}") String replyQueue,
            @Qualifier("mainSender") Sender sender,
            Receiver receiver
    ) {
        this.outputQueue = outputQueue;
        this.replyQueue = replyQueue;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Mono<JsonNode> sendAndReceive(JsonNode payload) {
        String correlationId = UUID.randomUUID().toString();

        OutboundMessage outboundMessage = new OutboundMessage(
                "",
                outputQueue,
                new AMQP.BasicProperties.Builder()
                        .correlationId(correlationId)
                        .replyTo(replyQueue)
                        .build(),
                toJsonBytes(payload)
        );

        return sender.declareQueue(QueueSpecification.queue(outputQueue))
                .then(sender.declareQueue(QueueSpecification.queue(replyQueue)))
                .then(sender.send(Mono.just(outboundMessage)))
                .thenMany(receiver.consumeAutoAck(replyQueue))
                .filter(delivery -> correlationId.equals(delivery.getProperties().getCorrelationId()))
                .next()
                .timeout(Duration.ofMillis(500))
                .flatMap(delivery -> fromJson(new String(delivery.getBody()), JsonNode.class));
    }

    public static byte[] toJsonBytes(JsonNode node) {
        try {
            return new ObjectMapper().writeValueAsBytes(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JsonNode to bytes", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        sender.close();
    }
}
