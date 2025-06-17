package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.r2dbch2sql.entity.PurchaseItem;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PurchaseItemCrudRepository extends ReactiveCrudRepository<PurchaseItem, Integer>,
        ReactiveQueryByExampleExecutor<PurchaseItem> {

    Flux<PurchaseItem> findAllByPurchaseId(Integer purchaseId);

}
