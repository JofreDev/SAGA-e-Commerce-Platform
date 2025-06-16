package co.com.sagacommerce.api.errorhandling;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import static co.com.sagacommerce.api.errorhandling.utils.ErrorResponse.buildErrorResponse;


@Component
@Slf4j
@Order(-3)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {


    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties resources,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, resources.getResources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.accept(MediaType.APPLICATION_JSON), this::renderErrorMessage);
    }


    @SneakyThrows
    private Mono<ServerResponse> renderErrorMessage(ServerRequest serverRequest) {
        return Mono.error(getError(serverRequest))
                .onErrorResume(Throwable.class, error -> {

                    log.error("Exception", error);

                    var body = buildErrorResponse(serverRequest, error);

                    return ServerResponse.status(body.status()).bodyValue(body);

                })
                .onErrorResume(error -> {
                    log.error("Error rendering exception", error);
                    var body = buildErrorResponse(serverRequest, error);
                    return ServerResponse.status(body.status()).bodyValue(body);
                }).cast(ServerResponse.class);
    }

}
