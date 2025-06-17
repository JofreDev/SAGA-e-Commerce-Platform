package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.r2dbch2sql.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductRepositoryAdapterTest {

    private ProductCrudRepository repository;
    private ObjectMapper objectMapper;
    private ProductRepositoryAdapter adapter;

    private final Product product = new Product(
            1, "Laptop", "Laptop Gamer",
            BigDecimal.valueOf(2000), 10, 1, 4.5, 100, true
    );

    private final ProductDTO productDTO = ProductDTO.builder()
            .productId("1")
            .title("Laptop")
            .description("Laptop Gamer")
            .price(BigDecimal.valueOf(2000))
            .stock(10)
            .categoryId(1)
            .rating(4.5)
            .reviewCount(100)
            .build();

    @BeforeEach
    void setUp() {
        repository = mock(ProductCrudRepository.class);
        objectMapper = mock(ObjectMapper.class);
        adapter = new ProductRepositoryAdapter(repository, objectMapper);
    }

    @Test
    void testFindById() {
        when(repository.findById(1)).thenReturn(Mono.just(product));
        when(objectMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        StepVerifier.create(adapter.findById(1))
                .expectNext(productDTO)
                .verifyComplete();
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Flux.just(product));
        when(objectMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        StepVerifier.create(adapter.findAll())
                .expectNext(productDTO)
                .verifyComplete();
    }

    @Test
    void testSave() {
        when(objectMapper.map(productDTO, Product.class)).thenReturn(product);
        when(repository.save(product)).thenReturn(Mono.just(product));
        when(objectMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        StepVerifier.create(adapter.save(productDTO))
                .expectNext(productDTO)
                .verifyComplete();
    }

    @Test
    void testFindAllByCategoryId() {
        when(repository.findAllByCategoryId(1)).thenReturn(Flux.just(product));
        when(objectMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        StepVerifier.create(adapter.findAllByCategoryId(1))
                .expectNext(productDTO)
                .verifyComplete();
    }

}