package co.com.sagacommerce.api.errorhandling.utils;


import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.model.validation.exceptions.SecurityException;
import co.com.sagacommerce.model.validation.exceptions.TechnicalException;
import co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage;
import co.com.sagacommerce.model.validation.exceptions.message.SecurityErrorMessage;
import co.com.sagacommerce.model.validation.exceptions.message.TechnicalErrorMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RenderUtils {

    public static final String CODE = "code";
    public static final String STATUS = "status";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";

    public static <T extends Throwable> String getAttribute(T exception, String key) {
        return switch (exception) {
            case TechnicalException ex -> getTechnicalExceptionAttribute(ex, key);
            case BusinessException ex -> getBusinessExceptionAttribute(ex, key);
            case SecurityException ex -> getSecurityExceptionAttribute(ex, key);
            default -> getGenericExceptionAttribute(exception, key);
        };
    }


    public static String getBusinessExceptionAttribute(BusinessException ex, String attributeName) {

        var error = Optional.ofNullable(ex.getBusinessErrorMessage());

        return switch (attributeName) {
            case CODE -> Optional.ofNullable(ex.getCode())
                    .or(() -> error.map(BusinessErrorMessage::getCode))
                    .orElse("");

            case STATUS -> String.valueOf(Optional.ofNullable(ex.getStatus())
                    .or(() -> error.map(BusinessErrorMessage::getStatus))
                    .orElse(HttpStatus.BAD_REQUEST.value()));

            case TITLE -> Optional.ofNullable(ex.getTitle())
                    .or(() -> error.map(BusinessErrorMessage::getTitle))
                    .orElse("");
            //message by default
            default -> Optional.ofNullable(ex.getMessage())
                    .or(() -> error.map(BusinessErrorMessage::getMessage))
                    .or(() -> Optional.ofNullable(ex.getDetail())).orElse("");
        };
    }

    public static String getTechnicalExceptionAttribute(TechnicalException ex, String attributeName) {

        var error = Optional.ofNullable(ex.getTechnicalErrorMessage());

        return switch (attributeName) {
            case CODE -> error.map(TechnicalErrorMessage::getCode).orElse("");

            case STATUS -> String.valueOf(error.map(TechnicalErrorMessage::getStatus)
                    .orElse(HttpStatus.BAD_GATEWAY.value()));

            case TITLE -> error.map(TechnicalErrorMessage::getTitle).orElse("");

            default -> Optional.ofNullable(ex.getMessage())
                    .or(() -> error.map(TechnicalErrorMessage::getMessage)).orElse("");
        };
    }

    public static String getSecurityExceptionAttribute(SecurityException ex, String attributeName) {

        var error = Optional.ofNullable(ex.getSecurityErrorMessage());

        return switch (attributeName) {
            case CODE -> error.map(SecurityErrorMessage::getCode).orElse("");

            case STATUS -> String.valueOf(error.map(SecurityErrorMessage::getStatus)
                    .orElse(HttpStatus.FORBIDDEN.value()));

            case TITLE -> error.map(SecurityErrorMessage::getTitle).orElse("");

            default -> Optional.ofNullable(ex.getMessage())
                    .or(() -> error.map(SecurityErrorMessage::getMessage)).orElse("");
        };
    }

    private static String getGenericExceptionAttribute(Throwable ex, String attributeName) {


        return switch (attributeName) {
            case CODE, STATUS -> String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());

            case TITLE -> ex.getClass().getSimpleName();
            //message by default
            default -> ex.getMessage();
        };
    }


}
