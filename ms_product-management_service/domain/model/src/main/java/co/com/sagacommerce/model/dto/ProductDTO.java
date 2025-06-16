package co.com.sagacommerce.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private String productId;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private int categoryId;
    private Double rating;
    private Integer reviewCount;

}
