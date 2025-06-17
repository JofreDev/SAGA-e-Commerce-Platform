package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.model.dto.CategoryDTO;
import co.com.sagacommerce.r2dbch2sql.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryRepositoryAdapterTest {


    private CategoryCrudRepository repository;
    private ObjectMapper objectMapper;
    private CategoryRepositoryAdapter adapter;

    private final Category category = new Category(1, "Tecnología", true);
    private final CategoryDTO categoryDTO = CategoryDTO.builder()
            .categoryId(1)
            .category("Tecnología")
            .active(true)
            .build();

    @BeforeEach
    void setUp() {
        repository = mock(CategoryCrudRepository.class);
        objectMapper = mock(ObjectMapper.class);
        adapter = new CategoryRepositoryAdapter(repository, objectMapper);
    }

    @Test
    void testFindById() {
        when(repository.findById(1)).thenReturn(Mono.just(category));
        when(objectMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        StepVerifier.create(adapter.findById(1))
                .expectNext(categoryDTO)
                .verifyComplete();
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Flux.just(category));
        when(objectMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        StepVerifier.create(adapter.findAll())
                .expectNext(categoryDTO)
                .verifyComplete();
    }

    @Test
    void testSave() {
        when(objectMapper.map(categoryDTO, Category.class)).thenReturn(category);
        when(repository.save(category)).thenReturn(Mono.just(category));
        when(objectMapper.map(category, CategoryDTO.class)).thenReturn(categoryDTO);

        StepVerifier.create(adapter.save(categoryDTO))
                .expectNext(categoryDTO)
                .verifyComplete();
    }

}