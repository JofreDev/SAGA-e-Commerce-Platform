package co.com.sagacommerce.mqlistener.config;

import com.rabbitmq.client.Connection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RabbitMQConnectionConfig.class)
class RabbitMQConnectionConfigTest {

    @Autowired
    private Mono<Connection> consumerConnectionMono;

    @Autowired
    private ReceiverOptions receiverOptions;

    @Autowired
    private Receiver receiver;

    @Test
    void shouldCreateConsumerConnectionMono() {
        assertThat(consumerConnectionMono).isNotNull();
    }

    @Test
    void shouldCreateReceiverOptions() {
        assertThat(receiverOptions).isNotNull();
        assertThat(receiverOptions.getConnectionMono()).isSameAs(consumerConnectionMono);
    }

    @Test
    void shouldCreateReceiver() {
        assertThat(receiver).isNotNull();
    }
}