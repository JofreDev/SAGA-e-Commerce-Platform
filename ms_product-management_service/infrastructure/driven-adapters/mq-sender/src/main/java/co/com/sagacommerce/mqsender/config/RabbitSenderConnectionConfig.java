package co.com.sagacommerce.mqsender.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
public class RabbitSenderConnectionConfig {

    @Bean
    Mono<Connection> connectionMono() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        return Mono.fromCallable(() -> connectionFactory
                .newConnection("product-management-sender-connection")).cache();
    }

    /*
     * To create the sender instance, it needs to receive a SenderOptions
     * When creating a new senderOptions, you need to set the connection (a mono) and the resource management scheduler.
     */
    @Bean
    public SenderOptions senderOptions(
            @Qualifier("connectionMono") Mono<Connection> connectionMono) {
        return new SenderOptions()
                .connectionMono(connectionMono)
                .resourceManagementScheduler(Schedulers.boundedElastic());
    }

    @Bean
    public Sender mainSender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }

    @Bean
    public Sender errorSender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }
}
