package co.com.sagacommerce.r2dbch2sql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("CATEGORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Category {

    @Id
    @Column("categoryId")
    private Integer categoryId;

    @Column("categoria")
    private String category;

    @Column("estado")
    private Boolean active;
}