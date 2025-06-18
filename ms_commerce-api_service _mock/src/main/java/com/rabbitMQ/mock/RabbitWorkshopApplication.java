package com.rabbitMQ.mock;

import com.rabbitmq.client.AMQP;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;


@SpringBootApplication
@RequiredArgsConstructor
@Log
public class RabbitWorkshopApplication {


    public static void main(String[] args) {

        SpringApplication.run(RabbitWorkshopApplication.class, args);

    }


}
