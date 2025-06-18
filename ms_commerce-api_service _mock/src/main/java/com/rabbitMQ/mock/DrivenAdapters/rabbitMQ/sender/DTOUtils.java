package com.rabbitMQ.mock.DrivenAdapters.rabbitMQ.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;


@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DTOUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @SneakyThrows
    public static <T> byte[] fromDTOToBytes(T clazz) {
        return OBJECT_MAPPER.writeValueAsBytes(clazz);
    }

    @SneakyThrows
    public static <T> byte[] fromDTOToBytes(T clazz, String correlationId, Throwable throwable) {

        ObjectNode node = OBJECT_MAPPER.valueToTree(clazz);
        node.put("error", throwable.getMessage());
        return OBJECT_MAPPER.writeValueAsBytes(node);

    }
}
