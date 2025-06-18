package co.com.sagacommerce.usecase.purchasetransaction;

import co.com.saga.commerce.model.gateways.PurchaseTransactionGateway;
import co.com.saga.commerce.model.gateways.repository.*;
import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.PURCHASE_ORDER_ERROR;
import static co.com.sagacommerce.usecase.purchasetransaction.PurchaseTransactionUseCase.STATE_PENDING;

@Log
@RequiredArgsConstructor
public class CompensationFailedPurchaseUseCase {

    private final PurchaseOrdersReaderRepository purchaseOrdersReaderRepository;
    private final PurchaseItemReaderRepository purchaseItemReaderRepository;
    private final PurchaseOrdersRepository purchaseOrdersRepository;
    private final PurchaseItemUpdaterRepository purchaseItemUpdaterRepository;
    private final ProductStockUpdaterRepository productStockUpdaterRepository;
    private final ProductStockReaderRepository productStockReaderRepository;
    private final PurchaseTransactionGateway purchaseTransactionGateway;

    public static final String STATE_CANCELED = "CANCELED";

    public Mono<Void> sendCanceledOrderEvent(PurchaseDTO purchaseRequest, String correlationId) {
        return purchaseOrdersReaderRepository.getPurchase(purchaseRequest.getId())
                .filter(purchase -> purchase.getState().equals(STATE_PENDING))
                .switchIfEmpty(Mono.error(
                        new BusinessException(PURCHASE_ORDER_ERROR, "The cancellation order is not valid")))
                .flatMap(purchaseOrder -> {
                    var purchaseId = purchaseOrder.getId();
                    return purchaseItemReaderRepository.getItemsByPurchase(purchaseId)
                            .collectList()
                            .flatMap(purchaseItems -> {
                                if (purchaseItems.isEmpty()) {
                                    return Mono.empty();
                                }

                                return Flux.fromIterable(purchaseItems)
                                        .flatMap(this::restorePreviousTotals)
                                        .then(purchaseItemUpdaterRepository.deletePurchaseItemOrder(purchaseId))
                                        .doOnSuccess(count -> log.info("Items successfully deleted"));
                            })
                            .then(purchaseOrdersRepository.deletePurchaseOrder(purchaseRequest))
                            .doOnSuccess(v -> log.info(String.format("Purchase order with id %s deleted "
                                    , purchaseRequest.getId())))
                            .then(Mono.defer(() -> {
                                purchaseRequest.setState(STATE_CANCELED);
                                return purchaseTransactionGateway.sendPurchaseOrder(purchaseRequest, correlationId);
                            }));
                });
    }

    private Mono<ProductDTO> restorePreviousTotals(PurchaseItemDTO purchaseItem) {
        return productStockReaderRepository.getProduct(purchaseItem.getProductId())
                .map(productDTO -> changeProductStock(purchaseItem, productDTO))
                .flatMap(productStockUpdaterRepository::updateStock);

    }

    private ProductDTO changeProductStock(PurchaseItemDTO purchaseItem, ProductDTO currentProduct) {
        currentProduct.setStock(currentProduct.getStock() + purchaseItem.getQuantity());
        return currentProduct;
    }
}
