package co.com.sagacommerce.model.validation.exceptions;

import co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void testConstructorWithExplicitFields() {
        String code = "B123";
        String detail = "Some detail";
        int status = 400;
        String title = "Custom Error";

        BusinessException exception = new BusinessException(code, detail, status, title);

        assertEquals(code, exception.getCode());
        assertEquals(detail, exception.getDetail());
        assertEquals(status, exception.getStatus());
        assertEquals(title, exception.getTitle());
        assertNull(exception.getBusinessErrorMessage());
    }

    @Test
    void testConstructorWithBusinessErrorMessageAndDetail() {
        BusinessErrorMessage errorMessage = BusinessErrorMessage.VALIDATION_DATA_ERROR;
        String detail = "Missing field 'productId'";

        BusinessException exception = new BusinessException(errorMessage, detail);

        assertEquals(errorMessage.getCode(), exception.getCode());
        assertEquals(errorMessage.getStatus(), exception.getStatus());
        assertEquals(errorMessage.getTitle(), exception.getTitle());
        assertEquals(errorMessage, exception.getBusinessErrorMessage());
        assertTrue(exception.getMessage().contains(errorMessage.getMessage()));
        assertTrue(exception.getMessage().contains(detail));
    }

    @Test
    void testConstructorWithBusinessErrorMessageAndCause() {
        BusinessErrorMessage errorMessage = BusinessErrorMessage.RESOURCE_NOT_FOUND;
        Throwable cause = new RuntimeException("ID not found");

        BusinessException exception = new BusinessException(errorMessage, cause);

        assertEquals(errorMessage.getCode(), exception.getCode());
        assertEquals(errorMessage.getStatus(), exception.getStatus());
        assertEquals(errorMessage.getTitle(), exception.getTitle());
        assertEquals(errorMessage, exception.getBusinessErrorMessage());
        assertTrue(exception.getMessage().contains(errorMessage.getMessage()));
        assertTrue(exception.getMessage().contains("ID not found"));
        assertEquals(cause, exception.getCause());
    }

}