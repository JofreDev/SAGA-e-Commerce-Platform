package co.com.sagacommerce.r2dbch2sql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("PURCHASE_ITEM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItem {

    @Id
    private Integer id;

    @Column("id_compra")
    private Integer purchaseId;

    @Column("id_producto")
    private Integer productId;

    @Column("cantidad")
    private Integer quantity;

    @Column("total")
    private BigDecimal total;

    @Column("estado")
    private Boolean active;


}
