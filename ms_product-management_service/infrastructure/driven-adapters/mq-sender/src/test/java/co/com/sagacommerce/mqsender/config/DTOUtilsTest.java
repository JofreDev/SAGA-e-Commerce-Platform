package co.com.sagacommerce.mqsender.config;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DTOUtilsTest {

    @Test
    void shouldSerializeDTOToBytesSuccessfully() {
        // Arrange
        PurchaseDTO dto = PurchaseDTO.builder()
                .id(1)
                .clientId("client-123")
                .date(LocalDateTime.of(2025, 6, 15, 10, 30))
                .paymentMethod("CREDIT_CARD")
                .comment("Primera compra")
                .state("APPROVED")
                .items(List.of(
                        new PurchaseItemDTO(1, 2,34, 250000.0, 1),
                        new PurchaseItemDTO(2, 1,34, 350000.0,2)
                ))
                .build();

        // Act
        byte[] result = DTOUtils.fromDTOToBytes(dto);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void shouldThrowExceptionWhenObjectIsNotSerializable() {
        assertThrows(Exception.class, () -> {
            Object notSerializable = new Object() {
                private final Thread thread = new Thread();
            };

            DTOUtils.fromDTOToBytes(notSerializable);
        });
    }
}