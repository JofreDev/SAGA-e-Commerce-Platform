package co.com.sagacommerce.usecase.purchasetransaction;

import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.saga.commerce.model.gateways.PurchaseTransactionGateway;
import co.com.saga.commerce.model.gateways.repository.ProductStockReaderRepository;
import co.com.saga.commerce.model.gateways.repository.ProductStockUpdaterRepository;
import co.com.saga.commerce.model.gateways.repository.PurchaseOrdersReaderRepository;
import co.com.saga.commerce.model.gateways.repository.PurchaseOrdersRepository;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.PURCHASE_ORDER_ERROR;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PurchaseTransactionUseCaseTest {

    @Mock
    private PurchaseOrdersRepository purchaseOrdersUpdater;
    @Mock
    private PurchaseOrdersReaderRepository purchaseOrdersReader;
    @Mock
    private ProductStockReaderRepository stockReader;
    @Mock
    private ProductStockUpdaterRepository productStockUpdater;
    @Mock
    private PurchaseTransactionGateway purchaseTransactionGateway;

    PurchaseDTO purchaseDTO;
    ProductDTO productDTO;

    private PurchaseTransactionUseCase purchaseTransactionUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        purchaseDTO = PurchaseDTO.builder()
                .id(0)
                .clientId("client-123")
                .date(LocalDateTime.parse("2025-06-15T10:30:00"))
                .paymentMethod("CREDIT_CARD")
                .comment("Primera compra con descuento")
                .items(List.of(
                        PurchaseItemDTO.builder().productId(1).quantity(2).purchaseId(1).build()
                ))
                .build();

        purchaseTransactionUseCase = new PurchaseTransactionUseCase(
                purchaseOrdersUpdater,
                purchaseOrdersReader,
                stockReader,
                productStockUpdater,
                purchaseTransactionGateway

        );

    }

    @Test
    void sendPurchaseOrderEvent() {

        productDTO = ProductDTO.builder()
                .productId("1")
                .title("Samsung SmartPhone A520 plus")
                .description("The best value for money cell phone on the market")
                .price(BigDecimal.valueOf(1700000))
                .stock(24)
                .categoryId(1)
                .rating(4.8)
                .reviewCount(45)
                .build();

        var newProduct = productDTO.toBuilder().stock(productDTO.getStock() - purchaseDTO.getItems().getFirst().getQuantity()).build();

        var purchaseSaved = purchaseDTO;
        purchaseSaved.setId(12);
        purchaseSaved.setState("PENDING");
        when(stockReader.getProduct(anyInt())).thenReturn(Mono.just(productDTO));
        when(productStockUpdater.updateStock(any(ProductDTO.class))).thenReturn(Mono.just(newProduct));
        when(purchaseOrdersUpdater.addPurchaseOrder(any(PurchaseDTO.class)))
                .thenReturn(Mono.just(purchaseSaved) );

        var purchaseOrder = purchaseSaved;
        purchaseOrder.setItems(List.of(PurchaseItemDTO.builder().purchaseId(1).productId(1).quantity(2).total(3400000).build()));

        when(purchaseOrdersReader.getPurchase(anyInt())).thenReturn(Mono.just(purchaseOrder));
        when(purchaseTransactionGateway.sendPurchaseOrder(any(PurchaseDTO.class),anyString()))
                .thenReturn(Mono.empty());


        purchaseTransactionUseCase.sendPurchaseOrderEvent(purchaseDTO, "423sfds-34534")
                .as(StepVerifier::create)
                .verifyComplete();

    }

    @Test
    void sendPurchaseOrderEventError() {

        productDTO = ProductDTO.builder()
                .productId("1")
                .title("Samsung SmartPhone A520 plus")
                .description("The best value for money cell phone on the market")
                .price(BigDecimal.valueOf(1700000))
                .stock(0)
                .categoryId(1)
                .rating(4.8)
                .reviewCount(45)
                .build();


        var purchaseSaved = purchaseDTO;
        purchaseSaved.setId(12);
        purchaseSaved.setState("PENDING");
        when(stockReader.getProduct(anyInt())).thenReturn(Mono.just(productDTO));


        purchaseTransactionUseCase.sendPurchaseOrderEvent(purchaseDTO, "423sfds-34534")
                .as(StepVerifier::create)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BusinessException.class, throwable);
                    Assertions.assertEquals(PURCHASE_ORDER_ERROR.getMessage(),((BusinessException) throwable).getBusinessErrorMessage().getMessage());
                    assertTrue(throwable.getMessage().contains("Requested quantity greater than current inventory"));
                    verify(productStockUpdater, never()).updateStock(any());
                    verify(purchaseTransactionGateway, never()).sendPurchaseOrder(any(), anyString());
                }).verify();

    }
}