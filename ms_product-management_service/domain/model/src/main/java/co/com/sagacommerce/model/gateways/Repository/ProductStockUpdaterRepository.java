package co.com.sagacommerce.model.gateways.Repository;

import co.com.sagacommerce.model.dto.ProductDTO;
import reactor.core.publisher.Mono;

public interface ProductStockUpdaterRepository {

    Mono<ProductDTO> updateStock(ProductDTO productDTO); // update -> Put
}
