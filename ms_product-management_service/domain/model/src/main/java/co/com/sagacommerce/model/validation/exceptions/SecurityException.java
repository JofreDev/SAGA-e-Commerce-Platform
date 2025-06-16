package co.com.sagacommerce.model.validation.exceptions;

import co.com.sagacommerce.model.validation.exceptions.message.SecurityErrorMessage;
import lombok.Getter;

@Getter
public class SecurityException extends RuntimeException {

    private final SecurityErrorMessage securityErrorMessage;


    public SecurityException(SecurityErrorMessage securityErrorMessage) {
        super(securityErrorMessage.getMessage());
        this.securityErrorMessage = securityErrorMessage;
    }

    public SecurityException(SecurityErrorMessage securityErrorMessage, Throwable cause) {
        super(securityErrorMessage.getMessage(), cause);
        this.securityErrorMessage = securityErrorMessage;
    }
}
