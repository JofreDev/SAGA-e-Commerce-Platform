package com.rabbitMQ.mock.DrivenAdapters.rabbitMQ.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;


@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static <T> Mono<T> fromJson(String json, Class<T> clazz) {
        return Mono.fromCallable(() -> OBJECT_MAPPER.readValue(json, clazz))
                .onErrorResume(error ->
                        Mono.error(new RuntimeException("Error fromJson"))
                );

    }

}
