package co.com.sagacommerce.model.validation;

import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    @Test
    void validate_withErrorvar_shouldThrowBusinessException_whenPredicateIsTrue() {
        var input = "";
        Predicate<String> isEmpty = String::isEmpty;
        var errorMessage = "El string está vacío";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ValidationService.validate(input, isEmpty, errorMessage);
        });

        assertTrue(exception.getMessage().contains(errorMessage));
    }

    @Test
    void validate_withErrorvar_shouldNotThrow_whenPredicateIsFalse() {
        var input = "data";
        Predicate<String> isEmpty = String::isEmpty;

        assertDoesNotThrow(() -> ValidationService.validate(input, isEmpty, "No debería lanzar excepción"));
    }

    @Test
    void validate_withErrorSupplier_shouldThrowBusinessException_whenPredicateIsTrue() {
        Integer value = -5;
        Predicate<Integer> isNegative = v -> v < 0;
        Supplier<String> errorSupplier = () -> "Valor negativo no permitido";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            ValidationService.validate(value, isNegative, errorSupplier);
        });

        assertTrue(exception.getMessage().contains("Valor negativo no permitido"));
    }

    @Test
    void validate_withErrorSupplier_shouldNotThrow_whenPredicateIsFalse() {
        Integer value = 10;
        Predicate<Integer> isNegative = v -> v < 0;

        assertDoesNotThrow(() -> ValidationService.validate(value, isNegative, () -> "No debe fallar"));
    }

}