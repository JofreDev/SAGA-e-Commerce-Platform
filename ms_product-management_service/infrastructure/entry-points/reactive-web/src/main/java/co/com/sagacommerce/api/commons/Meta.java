package co.com.sagacommerce.api.commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import static co.com.sagacommerce.api.commons.Headers.CONSUMER_ID;
import static co.com.sagacommerce.api.commons.Headers.MESSAGE_ID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta {

    @JsonProperty("_messageId")
    private String messageId;
    @JsonProperty("_requestDateTime")
    private String requestDateTime;
    @JsonProperty("_applicationId")
    private String application;


    public static Meta generateMeta(ServerRequest serverRequest) {
        return Meta.builder()
                .messageId(serverRequest.headers().firstHeader(MESSAGE_ID))
                .requestDateTime(ZonedDateTime.now(ZoneId.of("America/Bogota"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                                .withZone(ZoneId.systemDefault())))
                .application(serverRequest.headers().firstHeader(CONSUMER_ID))
                .build();
    }

    public static Meta generateMeta(Map<String,String> headers) {
        return Meta.builder()
                .messageId(Optional.ofNullable(headers.get(MESSAGE_ID)).orElse(""))
                .requestDateTime(ZonedDateTime.now(ZoneId.of("America/Bogota"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                                .withZone(ZoneId.systemDefault())))
                .application(Optional.ofNullable(headers.get(CONSUMER_ID)).orElse(""))
                .build();
    }




}
