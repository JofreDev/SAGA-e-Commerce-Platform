package co.com.sagacommerce.model.validation.exceptions;

import co.com.sagacommerce.model.validation.exceptions.message.TechnicalErrorMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TechnicalExceptionTest {

    @Test
    void testConstructorWithTechnicalErrorMessage() {
        // Arrange
        TechnicalErrorMessage errorMessage = TechnicalErrorMessage.DATABASE_INTERNAL_ERROR;

        // Act
        TechnicalException exception = new TechnicalException(errorMessage);

        // Assert
        assertEquals(errorMessage.getMessage(), exception.getMessage());
        assertEquals(errorMessage, exception.getTechnicalErrorMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithTechnicalErrorMessageAndCause() {
        // Arrange
        TechnicalErrorMessage errorMessage = TechnicalErrorMessage.INVALID_EXPRESSION;
        Throwable cause = new RuntimeException("Syntax error");

        // Act
        TechnicalException exception = new TechnicalException(errorMessage, cause);

        // Assert
        assertTrue(exception.getMessage().contains(errorMessage.getMessage()));
        assertTrue(exception.getMessage().contains("Syntax error"));
        assertEquals(cause, exception.getCause());
        assertEquals(errorMessage, exception.getTechnicalErrorMessage());
    }

    @Test
    void testConstructorWithTechnicalErrorMessageCauseAndDetail() {
        // Arrange
        TechnicalErrorMessage errorMessage = TechnicalErrorMessage.INVALID_QUERY_EXPRESSION;
        Throwable cause = new RuntimeException("Malformed JSON");
        String detail = "Missing bracket";

        // Act
        TechnicalException exception = new TechnicalException(errorMessage, cause, detail);

        // Assert
        assertTrue(exception.getMessage().contains(errorMessage.getMessage()));
        assertTrue(exception.getMessage().contains("Missing bracket"));
        assertEquals(cause, exception.getCause());
        assertEquals(errorMessage, exception.getTechnicalErrorMessage());
    }

    @Test
    void testConstructorWithTechnicalErrorMessageCauseAndBlankDetail() {
        // Arrange
        TechnicalErrorMessage errorMessage = TechnicalErrorMessage.INVALID_QUERY_EXPRESSION;
        Throwable cause = new RuntimeException("Invalid");

        // Act
        TechnicalException exception = new TechnicalException(errorMessage, cause, "   ");

        // Assert
        assertEquals(errorMessage.getMessage(), exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(errorMessage, exception.getTechnicalErrorMessage());
    }

}