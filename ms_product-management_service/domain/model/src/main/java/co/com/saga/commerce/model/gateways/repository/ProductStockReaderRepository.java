package co.com.saga.commerce.model.gateways.repository;

import co.com.sagacommerce.model.dto.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductStockReaderRepository {

    Flux<ProductDTO> getAllProducts();

    Flux<ProductDTO> getProductsByCategory(int categoryId);

    Mono<ProductDTO> getProduct(int productId);


}
