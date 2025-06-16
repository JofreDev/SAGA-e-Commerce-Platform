package co.com.sagacommerce.api;

import co.com.sagacommerce.api.errorhandling.GlobalErrorWebExceptionHandler;
import co.com.sagacommerce.api.errorhandling.utils.ErrorResponse;
import co.com.sagacommerce.api.handling.CategoryCatalogHandler;
import co.com.sagacommerce.api.handling.ProductCatalogHandler;
import co.com.sagacommerce.model.dto.CategoryDTO;
import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.model.validation.exceptions.SecurityException;
import co.com.sagacommerce.model.validation.exceptions.TechnicalException;
import co.com.sagacommerce.usecase.stockreader.CategoryStockReaderUseCase;
import co.com.sagacommerce.usecase.stockreader.ProductStockReaderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static co.com.sagacommerce.model.validation.exceptions.message.SecurityErrorMessage.CONSUMER_NOT_ALLOWED;
import static co.com.sagacommerce.model.validation.exceptions.message.TechnicalErrorMessage.DATABASE_INTERNAL_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, ProductCatalogHandler.class, CategoryCatalogHandler.class,
        GlobalErrorWebExceptionHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductStockReaderUseCase productStockReaderUseCase;
    @MockitoBean
    private CategoryStockReaderUseCase categoryStockReaderUseCase;

    private ProductDTO myProductDTO;

    private CategoryDTO myCategoryDTO;

    @BeforeEach
    void setUp() {

        myProductDTO = ProductDTO.builder()
                .productId("324")
                .stock(34)
                .title("my product")
                .price(BigDecimal.valueOf(235000))
                .categoryId(2)
                .description("Pretty samsung smartPhone")
                .reviewCount(12)
                .build();

        myCategoryDTO = CategoryDTO.builder()
                .categoryId(2)
                .category("technology")
                .active(true)
                .build();

    }

    /**
     * ProductCatalogHandler
     */

    @Test
    void listenGETAllProductsUseCaseTest() {


        when(productStockReaderUseCase.getAllProducts())
                .thenReturn(Flux.fromIterable(List.of(myProductDTO)));


        webTestClient.get()
                .uri("/api/products/all")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ProductDTO>>() {
                })
                .value(products -> {
                            assertNotNull(products);
                            assertNotNull(products);
                            assertEquals(1, products.size());
                            assertEquals(myProductDTO, products.getFirst());
                        }
                );
    }

    @Test
    void listenGETProductsByCategoryUseCaseTest() {


        when(productStockReaderUseCase.getAllProductsByCategory(anyInt()))
                .thenReturn(Flux.fromIterable(List.of(myProductDTO)));


        webTestClient.get()
                .uri("/api/products/category/2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ProductDTO>>() {
                })
                .value(products -> {
                            assertNotNull(products);
                            assertNotNull(products);
                            assertEquals(1, products.size());
                            assertEquals(myProductDTO, products.getFirst());
                        }
                );
    }

    @Test
    void listenGETProductsByCategoryUseCaseErrorTest() {


        final var ERROR_MESSAGE = "Validation data error : Invalid category id [ dos ]";


        webTestClient.get()
                .uri("/api/products/category/dos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {

                            assertNotNull(error);
                            assertEquals(400, error.status());

                            var errors = error.errors();
                            assertNotNull(errors);
                            assertFalse(errors.isEmpty());
                            var firstError = errors.getFirst();
                            assertEquals(ERROR_MESSAGE, firstError.detail());


                        }


                );
    }

    @Test
    void listenGETProductUseCaseTest() {


        when(productStockReaderUseCase.getSpecificProduct(anyInt()))
                .thenReturn(Mono.just(myProductDTO));


        webTestClient.get()
                .uri("/api/product/324")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductDTO.class)
                .value(product -> {
                            assertNotNull(product);
                            assertEquals(myProductDTO.getProductId(), product.getProductId());
                            assertEquals(myProductDTO, product);
                        }
                );
    }

    @Test
    void listenGETProductUseCaseBusinessErrorTest() {


        final var ERROR_MESSAGE = "Validation data error : Invalid product id [ dos ]";


        webTestClient.get()
                .uri("/api/product/dos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                            assertNotNull(error);
                            assertEquals(400, error.status());

                            var errors = error.errors();
                            assertNotNull(errors);
                            assertFalse(errors.isEmpty());
                            var firstError = errors.getFirst();
                            assertEquals(ERROR_MESSAGE, firstError.detail());


                        }


                );
    }

    @Test
    void listenGETProductUseCaseTechnicalErrorTest() {

        final var ERROR_MESSAGE = "Database internal error : Root cause";

        when(productStockReaderUseCase.getSpecificProduct(anyInt()))
                .thenReturn(Mono.error(new TechnicalException(DATABASE_INTERNAL_ERROR,
                        new RuntimeException("Root cause"))));



        webTestClient.get()
                .uri("/api/product/2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                            assertNotNull(error);
                            assertEquals(500, error.status());
                            var errors = error.errors();
                            assertNotNull(errors);
                            assertFalse(errors.isEmpty());
                            var firstError = errors.getFirst();
                            assertEquals(ERROR_MESSAGE, firstError.detail());


                        }


                );
    }

    @Test
    void listenGETProductUseCaseSecurityErrorTest() {


        when(productStockReaderUseCase.getSpecificProduct(anyInt()))
                .thenReturn(Mono.error(new SecurityException(CONSUMER_NOT_ALLOWED,
                        new RuntimeException("Root cause"))));


        webTestClient.get()
                .uri("/api/product/2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                            assertNotNull(error);
                            assertEquals(403, error.status());
                            var errors = error.errors();
                            assertNotNull(errors);
                            assertFalse(errors.isEmpty());
                            var firstError = errors.getFirst();
                            assertEquals(CONSUMER_NOT_ALLOWED.getMessage(), firstError.detail());


                        }


                );
    }

    /**
     * CategoryCatalogHandler
     */

    @Test
    void listenGETAllCategoriesUseCaseTest() {


        when(categoryStockReaderUseCase.getAllCategories())
                .thenReturn(Flux.fromIterable(List.of(myCategoryDTO)));


        webTestClient.get()
                .uri("/api/categories/all")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<CategoryDTO>>() {
                })
                .value(categories -> {
                            assertNotNull(categories);
                            assertNotNull(categories);
                            assertEquals(1, categories.size());
                            assertEquals(myCategoryDTO, categories.getFirst());
                        }
                );
    }

    @Test
    void listenGETCategoryUseCaseTest() {


        when(categoryStockReaderUseCase.getCategory(anyInt()))
                .thenReturn(Mono.just(myCategoryDTO));


        webTestClient.get()
                .uri("/api/category/2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryDTO.class)
                .value(category -> {
                            assertNotNull(category);
                            assertEquals(myCategoryDTO.getCategoryId(), category.getCategoryId());
                            assertEquals(myCategoryDTO, category);
                        }
                );
    }

    @Test
    void listenGETCategoryUseCaseBusinessErrorTest() {


        final var ERROR_MESSAGE = "Validation data error : Invalid category id [ dos ]";


        webTestClient.get()
                .uri("/api/category/dos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                            assertNotNull(error);
                            assertEquals(400, error.status());

                            var errors = error.errors();
                            assertNotNull(errors);
                            assertFalse(errors.isEmpty());
                            var firstError = errors.getFirst();
                            assertEquals(ERROR_MESSAGE, firstError.detail());


                        }


                );
    }

    @Test
    void listenGETCategoryUseCaseGenericErrorTest() {


        final var ERROR_MESSAGE = "This is a test error message";

        when(categoryStockReaderUseCase.getCategory(anyInt()))
                .thenReturn(Mono.error(new Exception(ERROR_MESSAGE,
                        new RuntimeException("Root cause"))));


        webTestClient.get()
                .uri("/api/category/2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .value(error -> {
                            assertEquals(500, error.status());
                            var errors = error.errors();
                            assertNotNull(errors);
                            assertFalse(errors.isEmpty());
                            var firstError = errors.getFirst();
                            assertEquals(ERROR_MESSAGE, firstError.detail());


                        }


                );
    }


}
