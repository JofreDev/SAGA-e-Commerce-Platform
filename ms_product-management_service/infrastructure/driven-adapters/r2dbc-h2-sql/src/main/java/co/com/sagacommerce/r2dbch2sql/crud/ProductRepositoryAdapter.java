package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.r2dbch2sql.entity.Product;
import co.com.sagacommerce.r2dbch2sql.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class ProductRepositoryAdapter extends ReactiveAdapterOperations<
        ProductDTO,
        Product,
        Integer,
        ProductCrudRepository
        > {

    public ProductRepositoryAdapter(ProductCrudRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, ProductDTO.class));
    }


    public Flux<ProductDTO> findAllByCategoryId(Integer categoryId) {
        return repository.findAllByCategoryId(categoryId)
                .map(this::toEntity);
    }
}
