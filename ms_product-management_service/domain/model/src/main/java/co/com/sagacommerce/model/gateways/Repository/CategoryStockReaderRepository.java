package co.com.sagacommerce.model.gateways.Repository;

import co.com.sagacommerce.model.dto.CategoryDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryStockReaderRepository {

    Mono<CategoryDTO> getCategory(int categoryId);

    Flux<CategoryDTO> getAllCategories();
}
