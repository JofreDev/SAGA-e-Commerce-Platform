package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.saga.commerce.model.gateways.repository.PurchaseItemReaderRepository;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.r2dbch2sql.crud.PurchaseRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.RESOURCE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseOrdersReaderRepositoryImpTest {

    private PurchaseRepositoryAdapter purchaseRepositoryAdapter;
    private PurchaseItemReaderRepository purchaseItemReaderRepository;
    private PurchaseOrdersReaderRepositoryImp repository;

    @BeforeEach
    void setUp() {
        purchaseRepositoryAdapter = mock(PurchaseRepositoryAdapter.class);
        purchaseItemReaderRepository = mock(PurchaseItemReaderRepository.class);
        repository = new PurchaseOrdersReaderRepositoryImp(purchaseRepositoryAdapter, purchaseItemReaderRepository);
    }

    @Test
    void shouldReturnPurchaseWithItems() {
        var purchase = PurchaseDTO.builder()
                .id(1)
                .clientId("123")
                .date(LocalDateTime.now())
                .paymentMethod("CARD")
                .build();

        var item1 = PurchaseItemDTO.builder().purchaseId(1).productId(10).quantity(1).build();
        var item2 = PurchaseItemDTO.builder().purchaseId(1).productId(20).quantity(2).build();

        when(purchaseRepositoryAdapter.findById(1)).thenReturn(Mono.just(purchase));
        when(purchaseItemReaderRepository.getItemsByPurchase(1)).thenReturn(Flux.just(item1, item2));

        repository.getPurchase(1)
                .as(StepVerifier::create)
                .expectNextMatches(p -> {
                    assertEquals(1, p.getId());
                    assertEquals(2, p.getItems().size());
                    return true;
                })
                .verifyComplete();

        verify(purchaseRepositoryAdapter).findById(1);
        verify(purchaseItemReaderRepository).getItemsByPurchase(1);
    }

    @Test
    void shouldReturnBusinessExceptionWhenPurchaseNotFound() {
        when(purchaseRepositoryAdapter.findById(1)).thenReturn(Mono.empty());

        repository.getPurchase(1)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertEquals(BusinessException.class, error.getClass());
                    assertEquals(RESOURCE_NOT_FOUND.getMessage(), ((BusinessException) error).getBusinessErrorMessage().getMessage());
                })
                .verify();

        verify(purchaseRepositoryAdapter).findById(1);
        verifyNoInteractions(purchaseItemReaderRepository);
    }

}