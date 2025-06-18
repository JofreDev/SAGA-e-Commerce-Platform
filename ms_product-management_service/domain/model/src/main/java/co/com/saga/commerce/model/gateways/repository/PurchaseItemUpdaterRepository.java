package co.com.saga.commerce.model.gateways.repository;

import reactor.core.publisher.Mono;

public interface PurchaseItemUpdaterRepository {

    Mono<Integer> deletePurchaseItemOrder(Integer purchaseItemId);

}
