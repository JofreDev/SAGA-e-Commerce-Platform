package co.com.sagacommerce.r2dbch2sql;

import co.com.saga.commerce.model.gateways.repository.PurchaseOrdersRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PurchaseOrdersUpdaterRepositoryImpTest {

    private PurchaseRepositoryAdapter purchaseRepositoryAdapter;
    private PurchaseItemUpdaterRepositoryImp purchaseItemUpdaterRepositoryImp;
    private PurchaseOrdersRepository repository;

    @BeforeEach
    void setUp() {
        purchaseRepositoryAdapter = mock(PurchaseRepositoryAdapter.class);
        purchaseItemUpdaterRepositoryImp = mock(PurchaseItemUpdaterRepositoryImp.class);
        repository = new PurchaseOrdersUpdaterRepositoryImp(purchaseRepositoryAdapter, purchaseItemUpdaterRepositoryImp);
    }

    @Test
    void shouldAddPurchaseOrderSuccessfully() {
        // Arrange
        var purchaseItem = new PurchaseItemDTO(null, 1, 22, 100.000,1);
        var purchaseDTO = new PurchaseDTO(1, "cliente123", LocalDateTime.now(), "PSE", "comentario", "ACTIVO", List.of(purchaseItem));

        when(purchaseRepositoryAdapter.save(purchaseDTO)).thenReturn(Mono.just(purchaseDTO));
        when(purchaseItemUpdaterRepositoryImp.addPurchaseItemOrder(any()))
                .thenReturn(Mono.just(purchaseItem));

        // Act
        var result = repository.addPurchaseOrder(purchaseDTO);

        // Assert
        StepVerifier.create(result)
                .expectNext(purchaseDTO)
                .verifyComplete();

        verify(purchaseRepositoryAdapter, times(1)).save(purchaseDTO);
        verify(purchaseItemUpdaterRepositoryImp, times(1)).addPurchaseItemOrder(any());
    }

    @Test
    void shouldReturnErrorWhenSaveReturnsEmpty() {
        // Arrange
        var purchaseDTO = new PurchaseDTO(1, "cliente123", LocalDateTime.now(), "PSE", "comentario", "ACTIVO", List.of());

        when(purchaseRepositoryAdapter.save(purchaseDTO)).thenReturn(Mono.empty());

        // Act
        var result = repository.addPurchaseOrder(purchaseDTO);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof TechnicalException &&
                                ((TechnicalException) throwable).getTechnicalErrorMessage() == DATABASE_TABLE_MODIFICATION_ERROR)
                .verify();

        verify(purchaseRepositoryAdapter, times(1)).save(purchaseDTO);
        verifyNoInteractions(purchaseItemUpdaterRepositoryImp);
    }
  
}