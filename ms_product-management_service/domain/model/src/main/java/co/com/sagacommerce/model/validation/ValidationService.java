package co.com.sagacommerce.model.validation;

import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.VALIDATION_DATA_ERROR;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationService {

    public static <T> void validate(T object, Predicate<T> predicate, String error) {
        if (predicate.test(object)) {
            throw new BusinessException(VALIDATION_DATA_ERROR, error);
        }
    }

    public static <T> void validate(T object, Predicate<T> predicate, Supplier<String> error) {
        if (predicate.test(object)) {
            throw new BusinessException(VALIDATION_DATA_ERROR, error.get());
        }
    }
}
