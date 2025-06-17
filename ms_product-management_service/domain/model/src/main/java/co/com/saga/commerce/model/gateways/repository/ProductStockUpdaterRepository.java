package co.com.saga.commerce.model.gateways.repository;

import co.com.sagacommerce.model.dto.ProductDTO;
import reactor.core.publisher.Mono;

public interface ProductStockUpdaterRepository {

    Mono<ProductDTO> updateStock(ProductDTO productDTO);
}
