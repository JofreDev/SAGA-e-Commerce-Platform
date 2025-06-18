package co.com.sagacommerce.usecase.purchasetransaction;

import co.com.saga.commerce.model.gateways.PurchaseTransactionGateway;
import co.com.saga.commerce.model.gateways.repository.*;
import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.PURCHASE_ORDER_ERROR;
import static co.com.sagacommerce.usecase.purchasetransaction.PurchaseTransactionUseCase.STATE_PENDING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CompensationFailedPurchaseUseCaseTest {

    private PurchaseOrdersReaderRepository purchaseOrdersReaderRepository;
    private PurchaseItemReaderRepository purchaseItemReaderRepository;
    private PurchaseOrdersRepository purchaseOrdersRepository;
    private PurchaseItemUpdaterRepository purchaseItemUpdaterRepository;
    private ProductStockUpdaterRepository productStockUpdaterRepository;
    private ProductStockReaderRepository productStockReaderRepository;
    private PurchaseTransactionGateway purchaseTransactionGateway;
    private CompensationFailedPurchaseUseCase useCase;

    @BeforeEach
    void setUp() {
        purchaseOrdersReaderRepository = mock(PurchaseOrdersReaderRepository.class);
        purchaseItemReaderRepository = mock(PurchaseItemReaderRepository.class);
        purchaseOrdersRepository = mock(PurchaseOrdersRepository.class);
        purchaseItemUpdaterRepository = mock(PurchaseItemUpdaterRepository.class);
        productStockUpdaterRepository = mock(ProductStockUpdaterRepository.class);
        productStockReaderRepository = mock(ProductStockReaderRepository.class);
        purchaseTransactionGateway = mock(PurchaseTransactionGateway.class);
        useCase = new CompensationFailedPurchaseUseCase(
                purchaseOrdersReaderRepository,
                purchaseItemReaderRepository,
                purchaseOrdersRepository,
                purchaseItemUpdaterRepository,
                productStockUpdaterRepository,
                productStockReaderRepository,
                purchaseTransactionGateway
        );
    }

    @Test
    void sendCanceledOrderEventSuccessfulShouldCancelOrderAndRestoreStock() {
        // Arrange
        var purchaseId = 1;
        var correlationId = "cid-123";
        var purchaseDTO = new PurchaseDTO();
        purchaseDTO.setId(purchaseId);
        purchaseDTO.setState(STATE_PENDING);

        var purchaseInDb = new PurchaseDTO();
        purchaseInDb.setId(purchaseId);
        purchaseInDb.setState(STATE_PENDING);

        var item1 = new PurchaseItemDTO();
        item1.setProductId(5);
        item1.setQuantity(3);

        var product = new ProductDTO();
        product.setProductId(String.valueOf(5L));
        product.setStock(7);

        when(purchaseOrdersReaderRepository.getPurchase(purchaseId)).thenReturn(Mono.just(purchaseInDb));
        when(purchaseItemReaderRepository.getItemsByPurchase(purchaseId)).thenReturn(Flux.just(item1));
        when(productStockReaderRepository.getProduct(item1.getProductId())).thenReturn(Mono.just(product));
        when(productStockUpdaterRepository.updateStock(any(ProductDTO.class))).thenReturn(Mono.just(product));
        when(purchaseItemUpdaterRepository.deletePurchaseItemOrder(purchaseId)).thenReturn(Mono.empty());
        when(purchaseOrdersRepository.deletePurchaseOrder(purchaseDTO)).thenReturn(Mono.empty());
        when(purchaseTransactionGateway.sendPurchaseOrder(any(PurchaseDTO.class), eq(correlationId))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.sendCanceledOrderEvent(purchaseDTO, correlationId))
                .verifyComplete();

        assertEquals(CompensationFailedPurchaseUseCase.STATE_CANCELED, purchaseDTO.getState());
        verify(purchaseOrdersReaderRepository).getPurchase(purchaseId);
        verify(purchaseItemReaderRepository).getItemsByPurchase(purchaseId);
        verify(productStockReaderRepository).getProduct(item1.getProductId());
        verify(productStockUpdaterRepository).updateStock(any(ProductDTO.class));
        verify(purchaseItemUpdaterRepository).deletePurchaseItemOrder(purchaseId);
        verify(purchaseOrdersRepository).deletePurchaseOrder(purchaseDTO);
        verify(purchaseTransactionGateway).sendPurchaseOrder(purchaseDTO, correlationId);
    }

    @Test
    void sendCanceledOrderEvent_purchaseNotFound_shouldReturnBusinessError() {
        var purchaseId = 123;
        var purchaseDTO = new PurchaseDTO();
        purchaseDTO.setId(purchaseId);
        var correlationId = "cid-456";

        when(purchaseOrdersReaderRepository.getPurchase(purchaseId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.sendCanceledOrderEvent(purchaseDTO, correlationId))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(PURCHASE_ORDER_ERROR, ((BusinessException) error).getBusinessErrorMessage());
                })
                .verify();

        verify(purchaseOrdersReaderRepository).getPurchase(purchaseId);
        verifyNoMoreInteractions(purchaseItemReaderRepository, purchaseItemUpdaterRepository, purchaseOrdersRepository, purchaseTransactionGateway);
    }

    @Test
    void sendCanceledOrderEvent_stateIsNotPending_shouldReturnBusinessError() {
        var purchaseId = 123;
        var purchaseDTO = new PurchaseDTO();
        purchaseDTO.setId(purchaseId);
        purchaseDTO.setState("CANCELED");
        var correlationId = "cid-456";

        var purchaseInDb = new PurchaseDTO();
        purchaseInDb.setId(purchaseId);
        purchaseInDb.setState("CANCELED");

        when(purchaseOrdersReaderRepository.getPurchase(purchaseId)).thenReturn(Mono.just(purchaseInDb));

        StepVerifier.create(useCase.sendCanceledOrderEvent(purchaseDTO, correlationId))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(PURCHASE_ORDER_ERROR, ((BusinessException) error).getBusinessErrorMessage());
                })
                .verify();
    }

    @Test
    void sendCanceledOrderEvent_noItems_shouldOnlyDeleteOrder() {
        var purchaseId = 444;
        var purchaseDTO = new PurchaseDTO();
        purchaseDTO.setId(purchaseId);
        purchaseDTO.setState(STATE_PENDING);
        var correlationId = "cid-000";

        var purchaseInDb = new PurchaseDTO();
        purchaseInDb.setId(purchaseId);
        purchaseInDb.setState(STATE_PENDING);

        when(purchaseOrdersReaderRepository.getPurchase(purchaseId)).thenReturn(Mono.just(purchaseInDb));
        when(purchaseItemReaderRepository.getItemsByPurchase(purchaseId)).thenReturn(Flux.empty());
        when(purchaseOrdersRepository.deletePurchaseOrder(purchaseDTO)).thenReturn(Mono.empty());
        when(purchaseTransactionGateway.sendPurchaseOrder(any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.sendCanceledOrderEvent(purchaseDTO, correlationId))
                .verifyComplete();

        verify(purchaseOrdersRepository).deletePurchaseOrder(purchaseDTO);
        verify(purchaseTransactionGateway).sendPurchaseOrder(purchaseDTO, correlationId);
        verifyNoMoreInteractions(productStockReaderRepository, productStockUpdaterRepository);
    }


}