package co.com.sagacommerce.model.validation.exceptions;

import co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private BusinessErrorMessage businessErrorMessage;

    private final String code;
    private final String detail;
    private final int status;
    private final String title ;

    public BusinessException(String code, String detail, int status, String title ) {
        this.code = code;
        this.detail = detail;
        this.status = status;
        this.title  = title ;
    }

    public BusinessException(BusinessErrorMessage businessErrorMessage, String detail) {
        super(businessErrorMessage.getMessage()
                .concat(
                        !detail.isBlank()
                                ? " : " + detail : ""
                ));
        this.businessErrorMessage = businessErrorMessage;
        this.code = businessErrorMessage.getCode();
        this.detail = businessErrorMessage.getMessage();
        this.status = businessErrorMessage.getStatus();
        this.title  = businessErrorMessage.getTitle();
    }

    public BusinessException(BusinessErrorMessage businessErrorMessage, Throwable cause) {
        super(businessErrorMessage.getMessage()
                .concat(" "+cause.getMessage()), cause);
        this.businessErrorMessage = businessErrorMessage;
        this.code = businessErrorMessage.getCode();
        this.detail = businessErrorMessage.getMessage();
        this.status = businessErrorMessage.getStatus();
        this.title  = businessErrorMessage.getTitle();
    }



}
