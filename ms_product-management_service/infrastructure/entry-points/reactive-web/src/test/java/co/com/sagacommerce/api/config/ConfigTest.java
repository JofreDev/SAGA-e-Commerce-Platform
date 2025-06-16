package co.com.sagacommerce.api.config;

import co.com.sagacommerce.api.errorhandling.GlobalErrorWebExceptionHandler;
import co.com.sagacommerce.api.handling.CategoryCatalogHandler;
import co.com.sagacommerce.api.handling.ProductCatalogHandler;
import co.com.sagacommerce.api.RouterRest;
import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.usecase.stockreader.CategoryStockReaderUseCase;
import co.com.sagacommerce.usecase.stockreader.ProductStockReaderUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, ProductCatalogHandler.class, CategoryCatalogHandler.class,
        GlobalErrorWebExceptionHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductStockReaderUseCase productStockReaderUseCase;

    @MockitoBean
    private CategoryStockReaderUseCase categoryStockReaderUseCase;

    @Test
    void corsConfigurationShouldAllowOrigins() {

        when(productStockReaderUseCase.getAllProducts())
                .thenReturn(Flux.fromIterable(List.of(
                        ProductDTO.builder()
                                .productId("324")
                                .stock(34)
                                .title("my product")
                                .price(BigDecimal.valueOf(235000))
                                .categoryId(2)
                                .description("Pretty samsung smartPhone")
                                .reviewCount(12)
                                .build()
                )));

        webTestClient.get()
                .uri("/api/products/all")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}