package co.com.sagacommerce.model.validation.exceptions;

import co.com.sagacommerce.model.validation.exceptions.message.TechnicalErrorMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final TechnicalErrorMessage technicalErrorMessage;

    public TechnicalException(TechnicalErrorMessage technicalErrorMessage) {
        super(technicalErrorMessage.getMessage());
        this.technicalErrorMessage = technicalErrorMessage;
    }

    public TechnicalException(TechnicalErrorMessage technicalErrorMessage, Throwable cause) {
        super(technicalErrorMessage.getMessage().concat(
                cause!=null ? " : ".concat(cause.getMessage()) : ""), cause);
        this.technicalErrorMessage = technicalErrorMessage;
    }

    public TechnicalException(TechnicalErrorMessage technicalErrorMessage, Throwable cause, String detail) {
        super(technicalErrorMessage.getMessage().concat(!detail.isBlank() ? " : ".concat(detail) : ""), cause);
        this.technicalErrorMessage = technicalErrorMessage;
    }
}
