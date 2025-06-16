package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.model.gateways.Repository.ProductStockUpdaterRepository;
import co.com.sagacommerce.r2dbch2sql.crud.ProductRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Log
@Repository
@RequiredArgsConstructor
public class ProductStockUpdaterRepositoryImp implements ProductStockUpdaterRepository {

    private final ProductRepositoryAdapter productRepositoryAdapter;


    @Override
    public Mono<ProductDTO> updateStock(ProductDTO productDTO) {
        return productRepositoryAdapter.save(productDTO);
    }
}
