package co.com.saga.commerce.model.gateways.repository;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PurchaseOrdersReaderRepository {

    Flux<PurchaseDTO> getAllPurchases();

    Mono<PurchaseDTO> getPurchase(int id);
}
