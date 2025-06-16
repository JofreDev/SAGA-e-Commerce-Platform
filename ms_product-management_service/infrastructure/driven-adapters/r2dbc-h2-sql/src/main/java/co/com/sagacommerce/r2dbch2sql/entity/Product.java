package co.com.sagacommerce.r2dbch2sql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("PRODUCT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Column("id_producto")
    private Integer productId;

    @Column("titulo")
    private String title;

    @Column("descripcion")
    private String description;

    @Column("precio")
    private BigDecimal price;

    @Column("cantidad")
    private Integer stock;

    @Column("categoryId")
    private Integer categoryId;

    @Column("calificacion")
    private Double rating;

    @Column("total_reviews")
    private Integer reviewCount;

    private Boolean state;
}
