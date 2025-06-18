package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.r2dbch2sql.entity.PurchaseItem;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PurchaseItemCrudRepository extends ReactiveCrudRepository<PurchaseItem, Integer>,
        ReactiveQueryByExampleExecutor<PurchaseItem> {

    Flux<PurchaseItem> findAllByPurchaseId(Integer purchaseId);

    @Query("DELETE FROM PURCHASE_ITEM WHERE id_compra = :purchaseId")
    Mono<Integer> deleteAllByPurchaseId(@Param("purchaseId") Integer purchaseId);

}
