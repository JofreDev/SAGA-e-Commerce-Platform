package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.r2dbch2sql.entity.Product;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductCrudRepository extends ReactiveCrudRepository<Product, Integer>,
        ReactiveQueryByExampleExecutor<Product> {

    Flux<Product> findAllByCategoryId(Integer categoryId);


}
