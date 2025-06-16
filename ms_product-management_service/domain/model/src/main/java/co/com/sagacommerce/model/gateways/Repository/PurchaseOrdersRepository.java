package co.com.sagacommerce.model.gateways.Repository;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import reactor.core.publisher.Mono;

public interface PurchaseOrdersRepository {

    Mono<PurchaseDTO> addPurchaseOrder(PurchaseDTO purchaseDTO); // save -> Post

}
