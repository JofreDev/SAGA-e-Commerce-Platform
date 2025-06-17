package co.com.sagacommerce.usecase.stockreader;

import co.com.sagacommerce.model.dto.CategoryDTO;
import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.saga.commerce.model.gateways.repository.CategoryStockReaderRepository;
import co.com.saga.commerce.model.gateways.repository.ProductStockReaderRepository;
import co.com.saga.commerce.model.gateways.repository.PurchaseItemReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ProductStockReaderUseCaseTest {

    @Mock
    private ProductStockReaderRepository productStockReader;
    @Mock
    private CategoryStockReaderRepository categoryStockReader;
    @Mock
    private PurchaseItemReaderRepository purchaseItemReaderRepository;

    private ProductStockReaderUseCase useCase;

    private ProductDTO mockProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ProductStockReaderUseCase(productStockReader, categoryStockReader, purchaseItemReaderRepository);

        mockProduct = ProductDTO.builder()
                .productId("1")
                .title("Producto de prueba")
                .description("Descripción")
                .price(BigDecimal.valueOf(1000))
                .stock(10)
                .categoryId(1)
                .rating(4.5)
                .reviewCount(12)
                .build();
    }

    @Test
    void shouldReturnAllProducts() {
        when(productStockReader.getAllProducts())
                .thenReturn(Flux.just(mockProduct));

        useCase.getAllProducts()
                .as(StepVerifier::create)
                .assertNext(product -> {
                    assertNotNull(product);
                    assertInstanceOf(ProductDTO.class, product);
                })
                .verifyComplete();

        verify(productStockReader, times(1)).getAllProducts();
    }

    @Test
    void shouldReturnProductsByCategory() {
        var category = CategoryDTO.builder().categoryId(1).category("Test Category").build();
        when(categoryStockReader.getCategory(anyInt()))
                .thenReturn(Mono.just(category));
        when(productStockReader.getProductsByCategory(anyInt()))
                .thenReturn(Flux.just(mockProduct));

        useCase.getAllProductsByCategory(1)
                .as(StepVerifier::create)
                .assertNext(product -> {
                    assertNotNull(product);
                    assertInstanceOf(ProductDTO.class, product);
                })
                .verifyComplete();

        verify(categoryStockReader, times(1)).getCategory(1);
        verify(productStockReader, times(1)).getProductsByCategory(1);
    }

    @Test
    void shouldReturnSpecificProduct() {
        when(productStockReader.getProduct(anyInt()))
                .thenReturn(Mono.just(mockProduct));

        useCase.getSpecificProduct(1)
                .as(StepVerifier::create)
                .assertNext(product -> {
                    assertNotNull(product);
                    assertInstanceOf(ProductDTO.class, product);
                })
                .verifyComplete();

        verify(productStockReader, times(1)).getProduct(1);
    }
}