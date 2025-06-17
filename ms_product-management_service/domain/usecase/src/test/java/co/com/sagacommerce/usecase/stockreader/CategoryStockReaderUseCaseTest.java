package co.com.sagacommerce.usecase.stockreader;

import co.com.sagacommerce.model.dto.CategoryDTO;
import co.com.saga.commerce.model.gateways.repository.CategoryStockReaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CategoryStockReaderUseCaseTest {

    private CategoryStockReaderUseCase categoryStockReaderUseCase;

    private CategoryStockReaderRepository categoryStockReaderRepository;

    @BeforeEach
    void setUp() {
        categoryStockReaderRepository= mock(CategoryStockReaderRepository.class);
        categoryStockReaderUseCase = new CategoryStockReaderUseCase(categoryStockReaderRepository);
    }

    @Test
    void getCategory() {

        when(categoryStockReaderRepository.getCategory(anyInt()))
                .thenReturn(Mono.just(mock(CategoryDTO.class)));

        categoryStockReaderUseCase.getCategory(2)
                .as(StepVerifier::create)
                .assertNext(rta ->{
                    assertNotNull(rta);
                    assertInstanceOf(CategoryDTO.class,rta);
                }).verifyComplete();

        verify(categoryStockReaderRepository, times(1)).getCategory(anyInt());
    }

    @Test
    void getAllCategories() {
        when(categoryStockReaderRepository.getAllCategories())
                .thenReturn(Flux.just(mock(CategoryDTO.class),mock(CategoryDTO.class)));

        categoryStockReaderUseCase.getAllCategories()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        verify(categoryStockReaderRepository, times(1)).getAllCategories();

    }
}