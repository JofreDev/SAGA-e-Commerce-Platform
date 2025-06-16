package co.com.sagacommerce.model.gateways.Repository;

import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import reactor.core.publisher.Flux;

public interface PurchaseItemReaderRepository {

    Flux<PurchaseItemDTO> getItemsByPurchase(int purchaseId);
}
