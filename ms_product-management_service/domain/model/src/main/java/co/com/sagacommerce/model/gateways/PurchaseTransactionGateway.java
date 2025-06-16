package co.com.sagacommerce.model.gateways;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import reactor.core.publisher.Mono;

public interface PurchaseTransactionGateway {

    Mono<Void> sendPurchaseOrder(PurchaseDTO purchaseOrder, String correlationId);
}
