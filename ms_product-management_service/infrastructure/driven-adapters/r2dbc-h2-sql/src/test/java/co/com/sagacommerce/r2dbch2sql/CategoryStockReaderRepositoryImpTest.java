package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.CategoryDTO;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.r2dbch2sql.crud.CategoryRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.RESOURCE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryStockReaderRepositoryImpTest {

    private CategoryRepositoryAdapter categoryRepositoryAdapter;
    private CategoryStockReaderRepositoryImp repository;

    @BeforeEach
    void setUp() {
        categoryRepositoryAdapter = mock(CategoryRepositoryAdapter.class);
        repository = new CategoryStockReaderRepositoryImp(categoryRepositoryAdapter);
    }

    @Test
    void shouldReturnCategoryWhenFound() {
        CategoryDTO category = CategoryDTO.builder().categoryId(1).category("Electronics").build();

        when(categoryRepositoryAdapter.findById(1)).thenReturn(Mono.just(category));

        repository.getCategory(1)
                .as(StepVerifier::create)
                .assertNext(c -> {
                    assertEquals(1, c.getCategoryId());
                    assertEquals("Electronics", c.getCategory());
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowWhenCategoryNotFound() {
        when(categoryRepositoryAdapter.findById(99)).thenReturn(Mono.empty());

        repository.getCategory(99)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    BusinessException ex = (BusinessException) error;
                    assertEquals(RESOURCE_NOT_FOUND, ex.getBusinessErrorMessage());
                    assertTrue(ex.getMessage().contains("Category id [ 99 ] not found"));
                })
                .verify();
    }

    @Test
    void shouldReturnAllCategories() {
        CategoryDTO cat1 = CategoryDTO.builder().categoryId(1).category("Electronics").build();
        CategoryDTO cat2 = CategoryDTO.builder().categoryId(2).category("Books").build();

        when(categoryRepositoryAdapter.findAll()).thenReturn(Flux.just(cat1, cat2));

        repository.getAllCategories()
                .as(StepVerifier::create)
                .expectNext(cat1)
                .expectNext(cat2)
                .verifyComplete();
    }

}