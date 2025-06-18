package co.com.saga.commerce.model.gateways.repository;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import reactor.core.publisher.Mono;

public interface PurchaseOrdersRepository {

    Mono<PurchaseDTO> addPurchaseOrder(PurchaseDTO purchaseDTO); // save -> Post
    Mono<Void> deletePurchaseOrder(PurchaseDTO purchaseDTO);

}
