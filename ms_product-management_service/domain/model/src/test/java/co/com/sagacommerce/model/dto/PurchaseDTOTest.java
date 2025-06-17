package co.com.sagacommerce.model.dto;

import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseDTOTest {

    @Test
    void shouldCreatePurchaseDTOWithValidData() {
        LocalDateTime now = LocalDateTime.now();
        PurchaseItemDTO item = new PurchaseItemDTO();
        List<PurchaseItemDTO> items = Collections.singletonList(item);

        PurchaseDTO purchase = new PurchaseDTO(1, "client123", now, "CREDIT", "Test comment", "NEW", items);

        assertEquals(1, purchase.getId());
        assertEquals("client123", purchase.getClientId());
        assertEquals(now, purchase.getDate());
        assertEquals("CREDIT", purchase.getPaymentMethod());
        assertEquals("Test comment", purchase.getComment());
        assertEquals("NEW", purchase.getState());
        assertEquals(items, purchase.getItems());
    }

    @Test
    void shouldFailWhenClientIdIsInvalid() {
        Exception exception = assertThrows(BusinessException.class, () ->
                new PurchaseDTO(1, "", LocalDateTime.now(), "CREDIT", "Test", "NEW", List.of(new PurchaseItemDTO()))
        );
        assertTrue(exception.getMessage().contains("Invalid clientId"));
    }

    @Test
    void shouldFailWhenPaymentMethodIsInvalid() {
        Exception exception = assertThrows(BusinessException.class, () ->
                new PurchaseDTO(1, "client123", LocalDateTime.now(), "", "Test", "NEW", List.of(new PurchaseItemDTO()))
        );
        assertTrue(exception.getMessage().contains("Invalid payment method"));
    }

    @Test
    void shouldFailWhenDateIsNull() {
        Exception exception = assertThrows(BusinessException.class, () ->
                new PurchaseDTO(1, "client123", null, "CREDIT", "Test", "NEW", List.of(new PurchaseItemDTO()))
        );
        assertTrue(exception.getMessage().contains("Invalid date"));
    }

    @Test
    void shouldFailWhenItemsIsNull() {
        Exception exception = assertThrows(BusinessException.class, () ->
                new PurchaseDTO(1, "client123", LocalDateTime.now(), "CREDIT", "Test", "NEW", null)
        );
        assertTrue(exception.getMessage().contains("Invalid items"));
    }

}