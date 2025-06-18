package com.rabbitMQ.mock.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.rabbitMQ.mock.DrivenAdapters.rabbitMQ.sender.SampleRabbitMQMessageSender;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@Log
@RestController
//@RequestMapping("/purchase")
@AllArgsConstructor
public class PurchaseController {

    private final SampleRabbitMQMessageSender sampleRabbitMQMessageSender;



    @PostMapping("/sendOrder")
    public Mono<ResponseEntity<JsonNode>> sendJsonAndGetResponse(@RequestBody JsonNode payload) {
        return sampleRabbitMQMessageSender.sendAndReceive(payload)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.severe("Request-Reply error: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(500).build());
                });
    }

}