package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.r2dbch2sql.entity.Category;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CategoryCrudRepository extends ReactiveCrudRepository<Category, Integer>, ReactiveQueryByExampleExecutor<Category> {
}
