package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.r2dbch2sql.crud.ProductRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.RESOURCE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductStockReaderRepositoryImpTest {

    private ProductRepositoryAdapter productRepositoryAdapter;
    private ProductStockReaderRepositoryImp repository;

    @BeforeEach
    void setUp() {
        productRepositoryAdapter = mock(ProductRepositoryAdapter.class);
        repository = new ProductStockReaderRepositoryImp(productRepositoryAdapter);
    }

    @Test
    void shouldReturnAllProducts() {
        var p1 = ProductDTO.builder().productId("1").title("TV").price(BigDecimal.valueOf(2000)).build();
        var p2 = ProductDTO.builder().productId("2").title("Laptop").price(BigDecimal.valueOf(4000)).build();

        when(productRepositoryAdapter.findAll()).thenReturn(Flux.just(p1, p2));

        repository.getAllProducts()
                .as(StepVerifier::create)
                .expectNext(p1)
                .expectNext(p2)
                .verifyComplete();
    }

    @Test
    void shouldReturnProductsByCategory() {
        ProductDTO p1 = ProductDTO.builder().productId("1").categoryId(10).title("TV").build();

        when(productRepositoryAdapter.findAllByCategoryId(10)).thenReturn(Flux.just(p1));

        repository.getProductsByCategory(10)
                .as(StepVerifier::create)
                .expectNext(p1)
                .verifyComplete();
    }

    @Test
    void shouldReturnProductWhenFound() {
        ProductDTO product = ProductDTO.builder().productId("1").title("Tablet").build();

        when(productRepositoryAdapter.findById(1)).thenReturn(Mono.just(product));

        repository.getProduct(1)
                .as(StepVerifier::create)
                .assertNext(p -> assertEquals("1", p.getProductId()))
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepositoryAdapter.findById(999)).thenReturn(Mono.empty());

        repository.getProduct(999)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    BusinessException ex = (BusinessException) error;
                    assertEquals(RESOURCE_NOT_FOUND, ex.getBusinessErrorMessage());
                    assertTrue(ex.getMessage().contains("Product id [ 999 ] not found"));
                })
                .verify();
    }

    @Test
    void shouldIgnoreNullProductFromDb() {
        when(productRepositoryAdapter.findById(1)).thenReturn(Mono.justOrEmpty(null));

        repository.getProduct(1)
                .as(StepVerifier::create)
                .expectError(BusinessException.class)
                .verify();
    }

}