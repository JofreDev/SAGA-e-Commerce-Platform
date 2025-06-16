package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.sagacommerce.r2dbch2sql.entity.PurchaseItem;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PurchaseItemCrudRepository extends ReactiveCrudRepository<PurchaseItem, UUID>,
        ReactiveQueryByExampleExecutor<PurchaseItem> {

    Flux<PurchaseItem> findAllByPurchaseId(Integer purchaseId);

}
