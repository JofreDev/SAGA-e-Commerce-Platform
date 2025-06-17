package co.com.sagacommerce.mqsender;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.saga.commerce.model.gateways.PurchaseTransactionGateway;
import com.rabbitmq.client.AMQP;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

import java.util.logging.Level;

import static co.com.sagacommerce.mqsender.config.DTOUtils.fromDTOToBytes;

@Log
@Component
public class SampleRabbitMQMessageSender implements PurchaseTransactionGateway {

    private final String outputQueue;
    private final Sender sender;

    public SampleRabbitMQMessageSender(@Value("{rabbit.mq.output-queue") String outputQueue,
                                       Sender sender) {
        this.sender = sender;
        this.outputQueue = outputQueue;
    }

    @Override
    public Mono<Void> sendPurchaseOrder(PurchaseDTO purchaseOrder, String correlationId) {

        Mono<OutboundMessage> outbound = Mono.fromCallable(() ->
                new OutboundMessage(
                        "",
                        outputQueue,
                        new AMQP.BasicProperties.Builder()
                                .correlationId(correlationId)
                                .build(),
                        fromDTOToBytes(purchaseOrder)
                )
        );

        return sender.declareQueue(QueueSpecification.queue(outputQueue))
                .then(sender.send(outbound))
                .doOnError(e -> log.log(Level.SEVERE, "Send failed", e));
    }

    @PreDestroy
    public void cleanup() {
        sender.close();
    }

}
