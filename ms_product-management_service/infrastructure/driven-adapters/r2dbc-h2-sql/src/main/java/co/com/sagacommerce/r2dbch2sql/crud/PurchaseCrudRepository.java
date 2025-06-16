package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.r2dbch2sql.entity.Purchase;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PurchaseCrudRepository extends ReactiveCrudRepository<Purchase, Integer>,
        ReactiveQueryByExampleExecutor<Purchase> {


}
