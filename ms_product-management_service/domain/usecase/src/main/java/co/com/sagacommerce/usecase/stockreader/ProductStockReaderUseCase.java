package co.com.sagacommerce.usecase.stockreader;

import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.saga.commerce.model.gateways.repository.CategoryStockReaderRepository;
import co.com.saga.commerce.model.gateways.repository.ProductStockReaderRepository;
import co.com.saga.commerce.model.gateways.repository.PurchaseItemReaderRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductStockReaderUseCase {

    private final ProductStockReaderRepository productStockReader;
    private final CategoryStockReaderRepository categoryStockReader;
    private final PurchaseItemReaderRepository purchaseItemReaderRepository;

    public Flux<ProductDTO> getAllProducts() {
        return productStockReader.getAllProducts();
    }

    public Flux<ProductDTO> getAllProductsByCategory(int id) {

        return  categoryStockReader.getCategory(id)
                .flatMapMany(c-> productStockReader.getProductsByCategory(c.getCategoryId())) ;
    }


    public Mono<ProductDTO> getSpecificProduct(int id) {
        return productStockReader.getProduct(id);
    }


}
