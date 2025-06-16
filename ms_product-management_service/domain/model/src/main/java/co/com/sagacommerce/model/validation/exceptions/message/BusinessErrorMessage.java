package co.com.sagacommerce.model.validation.exceptions.message;

import lombok.Getter;

@Getter
public enum BusinessErrorMessage {

    VALIDATION_DATA_ERROR("BP9001", "Validation data error", 400, "Bad Request"),
    PURCHASE_ORDER_ERROR("BP9101", "Invalid purchase order", 400, "Bad Request"),
    RESOURCE_NOT_FOUND("BP9101", "Record not found", 404, "Not Found");

    private final String code;
    private final String message;
    private final int status;
    private final String title;

    BusinessErrorMessage(String code, String message, int status, String title ) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.title  = title;
    }
}
