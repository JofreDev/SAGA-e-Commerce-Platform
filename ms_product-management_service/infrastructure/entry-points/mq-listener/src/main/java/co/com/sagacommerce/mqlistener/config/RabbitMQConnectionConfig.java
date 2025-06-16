package co.com.sagacommerce.mqlistener.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;

@Configuration
public class RabbitMQConnectionConfig {

    @Bean
    Mono<Connection> consumerConnectionMono() {
        var connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        return Mono.fromCallable(() -> connectionFactory
                .newConnection("product-management-receiver-connection")).cache();
    }

    @Bean
    public ReceiverOptions receiverOptions(
            @Qualifier("consumerConnectionMono") Mono<Connection> consumerConnectionMono) {
        return new ReceiverOptions()
                .connectionMono(consumerConnectionMono);
    }

    @Bean
    Receiver receiver(ReceiverOptions receiverOptions) {
        return RabbitFlux.createReceiver(receiverOptions);
    }
}
