package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.r2dbch2sql.crud.ProductRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductStockUpdaterRepositoryImpTest {

    private ProductRepositoryAdapter productRepositoryAdapter;
    private ProductStockUpdaterRepositoryImp repository;

    @BeforeEach
    void setUp() {
        productRepositoryAdapter = mock(ProductRepositoryAdapter.class);
        repository = new ProductStockUpdaterRepositoryImp(productRepositoryAdapter);
    }

    @Test
    void shouldUpdateStockSuccessfully() {
        ProductDTO product = ProductDTO.builder()
                .productId("1")
                .title("Monitor 4K")
                .price(BigDecimal.valueOf(1200000))
                .stock(50)
                .categoryId(3)
                .build();

        when(productRepositoryAdapter.save(product)).thenReturn(Mono.just(product));

        repository.updateStock(product)
                .as(StepVerifier::create)
                .assertNext(saved -> {
                    assertEquals("1", saved.getProductId());
                    assertEquals(50, saved.getStock());
                })
                .verifyComplete();

        verify(productRepositoryAdapter).save(product);
    }

}