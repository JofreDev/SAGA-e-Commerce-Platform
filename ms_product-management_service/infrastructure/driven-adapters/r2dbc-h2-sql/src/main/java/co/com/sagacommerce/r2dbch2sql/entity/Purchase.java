package co.com.sagacommerce.r2dbch2sql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("PURCHASE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Purchase {

    @Id
    @Column("id_compra")
    private Integer id;

    @Column("id_cliente")
    private String clientId;

    @Column("fecha")
    private LocalDateTime date;

    @Column("medio_pago")
    private String paymentMethod;

    @Column("comentario")
    private String comment;

    @Column("estado")
    private String state;
}
