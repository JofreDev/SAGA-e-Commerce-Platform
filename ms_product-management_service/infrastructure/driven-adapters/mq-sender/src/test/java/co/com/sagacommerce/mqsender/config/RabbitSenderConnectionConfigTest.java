package co.com.sagacommerce.mqsender.config;

import com.rabbitmq.client.Connection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = RabbitSenderConnectionConfig.class)
class RabbitSenderConnectionConfigTest {

    @Autowired
    private Mono<Connection> senderConnectionMono;

    @Autowired
    private SenderOptions senderOptions;

    @Autowired
    private Sender sender;

    @Test
    void shouldCreateSenderConnectionMono() {
        assertThat(senderConnectionMono).isNotNull();
    }

    @Test
    void shouldCreateSenderOptions() {
        assertThat(senderOptions).isNotNull();
        assertThat(senderOptions.getConnectionMono()).isSameAs(senderConnectionMono);
    }

    @Test
    void shouldCreateReceiver() {
        assertThat(sender).isNotNull();
    }

}