package co.com.saga.commerce.model.gateways.repository;

import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import reactor.core.publisher.Flux;

public interface PurchaseItemReaderRepository {

    Flux<PurchaseItemDTO> getItemsByPurchase(int purchaseId);
}
