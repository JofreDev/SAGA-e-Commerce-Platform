package co.com.sagacommerce.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static co.com.sagacommerce.model.validation.ValidationService.validate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class PurchaseItemDTO {

    private Integer id;
    private Integer productId;
    private int quantity;
    private double total;
    private Integer purchaseId;

    public PurchaseItemDTO(Integer id, Integer productId, int quantity, double total, int purchaseId) {
        validate(productId, Objects::isNull, "Invalid reference to productId");
        validate(quantity, Objects::isNull, "Invalid quantity");

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.total = total;
        this.purchaseId = purchaseId;
    }
}
