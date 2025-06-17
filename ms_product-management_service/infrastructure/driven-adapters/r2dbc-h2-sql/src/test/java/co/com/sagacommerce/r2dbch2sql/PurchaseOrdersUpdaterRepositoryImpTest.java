package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.sagacommerce.model.validation.exceptions.TechnicalException;
import co.com.sagacommerce.r2dbch2sql.crud.PurchaseRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static co.com.sagacommerce.model.validation.exceptions.message.TechnicalErrorMessage.DATABASE_TABLE_MODIFICATION_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurchaseOrdersUpdaterRepositoryImpTest {

    private PurchaseRepositoryAdapter purchaseRepositoryAdapter;
    private PurchaseItemUpdaterRepositoryImp purchaseItemUpdaterRepository;
    private PurchaseOrdersUpdaterRepositoryImp repository;

    @BeforeEach
    void setUp() {
        purchaseRepositoryAdapter = mock(PurchaseRepositoryAdapter.class);
        purchaseItemUpdaterRepository = mock(PurchaseItemUpdaterRepositoryImp.class);
        repository = new PurchaseOrdersUpdaterRepositoryImp(purchaseRepositoryAdapter, purchaseItemUpdaterRepository);
    }

    @Test
    void shouldAddPurchaseOrderSuccessfully() {
        var item1 = PurchaseItemDTO.builder().id(1).productId(1).quantity(2).purchaseId(1).build();
        var item2 = PurchaseItemDTO.builder().id(2).productId(2).quantity(1).purchaseId(1).build();

        var purchase = PurchaseDTO.builder()
                .id(1)
                .clientId("client-123")
                .date(LocalDateTime.now())
                .paymentMethod("CASH")
                .items(List.of(item1, item2))
                .build();

        var savedPurchase = purchase.toBuilder().id(10).build();

        when(purchaseRepositoryAdapter.save(purchase)).thenReturn(Mono.just(savedPurchase));
        when(purchaseItemUpdaterRepository.addPurchaseItemOrder(any()))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        repository.addPurchaseOrder(purchase)
                .as(StepVerifier::create)
                .expectNextMatches(result -> result.getId() == 10 && result.getItems().size() == 2)
                .verifyComplete();

        verify(purchaseRepositoryAdapter).save(purchase);
        verify(purchaseItemUpdaterRepository, times(2)).addPurchaseItemOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenSaveFails() {
        var purchase = PurchaseDTO.builder()
                .id(1)
                .clientId("client-123")
                .date(LocalDateTime.now())
                .paymentMethod("CASH")
                .items(List.of())
                .build();

        when(purchaseRepositoryAdapter.save(purchase)).thenReturn(Mono.empty());

        repository.addPurchaseOrder(purchase)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertEquals(TechnicalException.class, error.getClass());
                    assertEquals(DATABASE_TABLE_MODIFICATION_ERROR.getMessage(),
                            ((TechnicalException) error).getTechnicalErrorMessage().getMessage());
                })
                .verify();

        verify(purchaseRepositoryAdapter).save(purchase);
        verifyNoInteractions(purchaseItemUpdaterRepository);
    }

}