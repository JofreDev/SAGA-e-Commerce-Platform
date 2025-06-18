package co.com.sagacommerce.usecase.purchasetransaction;

import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.saga.commerce.model.gateways.PurchaseTransactionGateway;
import co.com.saga.commerce.model.gateways.repository.ProductStockUpdaterRepository;
import co.com.saga.commerce.model.gateways.repository.PurchaseOrdersReaderRepository;
import co.com.saga.commerce.model.gateways.repository.PurchaseOrdersRepository;
import co.com.saga.commerce.model.gateways.repository.ProductStockReaderRepository;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.PURCHASE_ORDER_ERROR;

@Log
@RequiredArgsConstructor
public class PurchaseTransactionUseCase {

    private final PurchaseOrdersRepository purchaseOrdersUpdater;
    private final PurchaseOrdersReaderRepository purchaseOrdersReader;
    private final ProductStockReaderRepository stockReader;
    private final ProductStockUpdaterRepository productStockUpdater;
    private final PurchaseTransactionGateway purchaseTransactionGateway;

    public static final String STATE_PENDING = "PENDING";

    public Mono<Void> sendPurchaseOrderEvent(PurchaseDTO purchaseRequest, String correlationId) {

        return generatePurchaseOrder(purchaseRequest)
                .flatMap(prePurchaseOrder -> purchaseOrdersReader
                        .getPurchase(prePurchaseOrder.getId()))
                .flatMap(purchaseOrder ->
                        purchaseTransactionGateway.sendPurchaseOrder(purchaseOrder, correlationId));

    }


    private Mono<PurchaseDTO> generatePurchaseOrder(PurchaseDTO purchaseRequest) {

        return updateStock(Flux.fromIterable(purchaseRequest.getItems()))
                .collectList().map(products -> {
                    enrichWithTotals(products,purchaseRequest);
                    return purchaseRequest;
                })
                .flatMap(purchase -> {
                    purchaseRequest.setState(STATE_PENDING);
                    return purchaseOrdersUpdater.addPurchaseOrder(purchaseRequest);

                });

    }

    private Flux<ProductDTO> updateStock(Flux<PurchaseItemDTO> purchaseItems) {
        return purchaseItems.flatMap(
                        purchaseItem -> stockReader.getProduct(purchaseItem.getProductId())
                                .filter(productDTO -> purchaseItem.getQuantity() <= productDTO.getStock())
                                .switchIfEmpty(Mono.error(
                                        new BusinessException(
                                                PURCHASE_ORDER_ERROR,
                                                "Requested quantity greater than current inventory")
                                ))
                                .doOnNext(p -> log.info(
                                        String.format("Product %s with current stock : %S ", p.getTitle(), p.getStock())))
                                .map(productDTO -> changeProductStock(purchaseItem, productDTO))
                                .flatMap(productStockUpdater::updateStock))
                .doOnNext(p -> log.info(
                        String.format("Product %s with new stock : %S ", p.getTitle(), p.getStock())));

    }


    private void enrichWithTotals(List<ProductDTO> products, PurchaseDTO purchaseRequest) {
        products.forEach(product ->
                purchaseRequest.getItems().stream()
                        .filter(item -> item.getProductId() == Integer.parseInt(product.getProductId()))
                        .findFirst()
                        .ifPresent(item ->
                                item.setTotal(item.getQuantity() * product.getPrice().doubleValue())
                        )
        );
    }

    private ProductDTO changeProductStock(PurchaseItemDTO purchaseItem, ProductDTO currentProduct) {
        currentProduct.setStock(currentProduct.getStock() - purchaseItem.getQuantity());
        return currentProduct;
    }


}
