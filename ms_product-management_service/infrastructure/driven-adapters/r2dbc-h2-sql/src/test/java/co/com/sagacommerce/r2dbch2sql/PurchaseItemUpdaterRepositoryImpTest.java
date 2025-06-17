package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.sagacommerce.model.validation.exceptions.TechnicalException;
import co.com.sagacommerce.r2dbch2sql.crud.PurchaseItemRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.sagacommerce.model.validation.exceptions.message.TechnicalErrorMessage.DATABASE_TABLE_MODIFICATION_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseItemUpdaterRepositoryImpTest {

    private PurchaseItemRepositoryAdapter purchaseItemRepositoryAdapter;
    private PurchaseItemUpdaterRepositoryImp repository;

    @BeforeEach
    void setUp() {
        purchaseItemRepositoryAdapter = mock(PurchaseItemRepositoryAdapter.class);
        repository = new PurchaseItemUpdaterRepositoryImp(purchaseItemRepositoryAdapter);
    }

    @Test
    void shouldSavePurchaseItemSuccessfully() {
        var item = PurchaseItemDTO.builder()
                .purchaseId(1)
                .productId(100)
                .quantity(2)
                .total(4000.0)
                .build();

        when(purchaseItemRepositoryAdapter.save(item)).thenReturn(Mono.just(item));

        repository.addPurchaseItemOrder(item)
                .as(StepVerifier::create)
                .expectNext(item)
                .verifyComplete();

        verify(purchaseItemRepositoryAdapter).save(item);
    }

    @Test
    void shouldReturnTechnicalExceptionWhenSaveFails() {
        var item = PurchaseItemDTO.builder()
                .purchaseId(1)
                .productId(100)
                .quantity(2)
                .total(4000.0)
                .build();

        RuntimeException dbException = new RuntimeException("DB error");
        when(purchaseItemRepositoryAdapter.save(item)).thenReturn(Mono.error(dbException));

        repository.addPurchaseItemOrder(item)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertEquals(TechnicalException.class, error.getClass());
                    assertEquals(DATABASE_TABLE_MODIFICATION_ERROR.getMessage(), ((TechnicalException) error).getTechnicalErrorMessage().getMessage());
                })
                .verify();

        verify(purchaseItemRepositoryAdapter).save(item);
    }

}