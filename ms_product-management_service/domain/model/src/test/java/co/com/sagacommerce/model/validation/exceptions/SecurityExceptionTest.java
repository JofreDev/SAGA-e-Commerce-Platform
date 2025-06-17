package co.com.sagacommerce.model.validation.exceptions;

import co.com.sagacommerce.model.validation.exceptions.message.SecurityErrorMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityExceptionTest {
    @Test
    void testConstructorWithSecurityErrorMessage() {
        // Arrange
        SecurityErrorMessage errorMessage = SecurityErrorMessage.CONSUMER_NOT_ALLOWED;

        // Act
        SecurityException exception = new SecurityException(errorMessage);

        // Assert
        assertEquals(errorMessage.getMessage(), exception.getMessage());
        assertEquals(errorMessage, exception.getSecurityErrorMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithSecurityErrorMessageAndCause() {
        // Arrange
        SecurityErrorMessage errorMessage = SecurityErrorMessage.CONSUMER_NOT_ALLOWED;
        Throwable cause = new RuntimeException("Unauthorized attempt");

        // Act
        SecurityException exception = new SecurityException(errorMessage, cause);

        // Assert
        assertEquals(errorMessage.getMessage(), exception.getMessage());
        assertEquals(errorMessage, exception.getSecurityErrorMessage());
        assertEquals(cause, exception.getCause());
    }


}