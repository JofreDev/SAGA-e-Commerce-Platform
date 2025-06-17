package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.r2dbch2sql.crud.PurchaseItemRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.RESOURCE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseItemReaderRepositoryImpTest {

    private PurchaseItemRepositoryAdapter purchaseItemRepositoryAdapter;
    private PurchaseItemReaderRepositoryImp repository;

    @BeforeEach
    void setUp() {
        purchaseItemRepositoryAdapter = mock(PurchaseItemRepositoryAdapter.class);
        repository = new PurchaseItemReaderRepositoryImp(purchaseItemRepositoryAdapter);
    }

    @Test
    void shouldReturnItemsByPurchaseSuccessfully() {
        var item = PurchaseItemDTO.builder()
                .purchaseId(1)
                .productId(100)
                .quantity(3)
                .total(6000.0)
                .build();

        when(purchaseItemRepositoryAdapter.findAllByPurchaseId(1))
                .thenReturn(Flux.fromIterable(List.of(item)));

        repository.getItemsByPurchase(1)
                .as(StepVerifier::create)
                .expectNext(item)
                .verifyComplete();

        verify(purchaseItemRepositoryAdapter).findAllByPurchaseId(1);
    }

    @Test
    void shouldThrowExceptionWhenNoItemsFound() {
        when(purchaseItemRepositoryAdapter.findAllByPurchaseId(99))
                .thenReturn(Flux.empty());

        repository.getItemsByPurchase(99)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertEquals(BusinessException.class, error.getClass());
                    assertEquals(RESOURCE_NOT_FOUND.getMessage(), ((BusinessException) error).getBusinessErrorMessage().getMessage());
                })
                .verify();

        verify(purchaseItemRepositoryAdapter).findAllByPurchaseId(99);
    }

}