package co.com.sagacommerce.model.gateways.Repository;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PurchaseOrdersReaderRepository {

    Flux<PurchaseDTO> getAllPurchases();

    Mono<PurchaseDTO> getPurchase(int id);
}
