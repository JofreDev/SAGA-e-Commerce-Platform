package co.com.sagacommerce.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static co.com.sagacommerce.model.validation.ValidationService.validate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class PurchaseDTO {

    private Integer id;
    private String clientId;
    private LocalDateTime date;
    private String paymentMethod;
    private String comment;
    private String state;
    private List<PurchaseItemDTO> items;

    public PurchaseDTO(int id, String clientId, LocalDateTime date, String paymentMethod, String comment,
                       String state, List<PurchaseItemDTO> items) {
        Predicate<String> isNullOrBlank = str -> str == null || str.isBlank();

        validate(clientId, isNullOrBlank, "Invalid clientId");
        validate(paymentMethod, isNullOrBlank, "Invalid payment method");
        validate(date, Objects::isNull, "Invalid date");
        validate(clientId, Objects::isNull, "Invalid items");
        this.id = id;
        this.clientId = clientId;
        this.date = date;
        this.paymentMethod = paymentMethod;
        this.comment = comment;
        this.state = state;
        this.items = items;
    }




}
