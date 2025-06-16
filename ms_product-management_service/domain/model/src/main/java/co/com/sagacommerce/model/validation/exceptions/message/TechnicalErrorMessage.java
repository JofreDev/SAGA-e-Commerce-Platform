package co.com.sagacommerce.model.validation.exceptions.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechnicalErrorMessage {

    INVALID_EXPRESSION("SP9110", "Invalid expression", 400, "Invalid Input"),
    INVALID_QUERY_EXPRESSION("SP9111", "Invalid query expression in JSON", 400, "Invalid Input"),
    INVALID_NUMBER("SP9112", "Invalid number format", 400, "Invalid Input"),
    JSON_SERIALIZATION_ERROR("","",400,""),
    DATABASE_INTERNAL_ERROR("SP200", "Database internal error",500 ,"Internal Server Error"),
    DATABASE_TABLE_MODIFICATION_ERROR("SP201", "Database insertion error",500 ,"Internal Server Error");

    private final String code;
    private final String message;
    private final Integer status;
    private final String title;

}
