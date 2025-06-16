package co.com.sagacommerce.api.errorhandling.utils;


import co.com.sagacommerce.api.commons.Meta;
import co.com.sagacommerce.api.errorhandling.CustomError;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;

import static co.com.sagacommerce.api.errorhandling.utils.RenderUtils.*;


public record ErrorResponse(Meta meta, String title, int status, List<CustomError> errors) {

    public static ErrorResponse buildErrorResponse(
            ServerRequest request, Throwable error) {
        return new ErrorResponse(
                Meta.generateMeta(request),
                getAttribute(error, TITLE),
                Integer.parseInt(getAttribute(error, STATUS)),
                List.of(new CustomError(getAttribute(error, CODE), getAttribute(error, MESSAGE)))
        );
    }
}
