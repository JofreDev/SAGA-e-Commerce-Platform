package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.ProductDTO;
import co.com.sagacommerce.model.gateways.Repository.ProductStockReaderRepository;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.r2dbch2sql.crud.ProductRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.RESOURCE_NOT_FOUND;

@Log
@Repository
@RequiredArgsConstructor
public class ProductStockReaderRepositoryImp implements ProductStockReaderRepository{

    private final ProductRepositoryAdapter productRepositoryAdapter;


    @Override
    public Flux<ProductDTO> getAllProducts() {
        return productRepositoryAdapter.findAll();
    }

    @Override
    public Flux<ProductDTO> getProductsByCategory(int categoryId) {
        return productRepositoryAdapter.findAllByCategoryId(categoryId);
    }
    @Override
    public Mono<ProductDTO> getProduct(int productId) {
        return productRepositoryAdapter.findById(productId)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(() -> new BusinessException(RESOURCE_NOT_FOUND,
                        String.format("Product id [ %S ] not found ", productId))));
    }



}
