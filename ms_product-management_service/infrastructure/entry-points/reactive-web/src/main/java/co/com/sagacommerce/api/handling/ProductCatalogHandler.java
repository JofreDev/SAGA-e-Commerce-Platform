package co.com.sagacommerce.api.handling;

import co.com.sagacommerce.api.commons.FormatUtils;
import co.com.sagacommerce.usecase.stockreader.ProductStockReaderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.sagacommerce.model.validation.ValidationService.validate;

@Component
@RequiredArgsConstructor
public class ProductCatalogHandler {

    private final ProductStockReaderUseCase productStockReaderUseCase;

    /**
     * For all listeners
     * @return Can and only returns product information.
     */

    public Mono<ServerResponse> listenGETAllProductsUseCase(ServerRequest serverRequest) {
        return productStockReaderUseCase.getAllProducts()
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products));
    }

    public Mono<ServerResponse> listenGETProductsByCategoryUseCase(ServerRequest serverRequest) {
        var categoryId = serverRequest.pathVariable("idCategory");
        validate(categoryId, FormatUtils::isNumber, String.format("Invalid category id [ %s ]",categoryId) );
        return productStockReaderUseCase.getAllProductsByCategory(Integer.parseInt(categoryId))
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products));
    }

    public Mono<ServerResponse> listenGETProductUseCase(ServerRequest serverRequest) {
        var productId = serverRequest.pathVariable("id");
        validate(productId, FormatUtils::isNumber, String.format("Invalid product id [ %s ]",productId) );
        return productStockReaderUseCase.getSpecificProduct(Integer.parseInt(productId))
                .flatMap(p -> ServerResponse.ok().bodyValue(p));
    }
/*
    public Mono<ServerResponse> listenGETPurchaseItemUseCase(ServerRequest serverRequest) {
        var productId = serverRequest.pathVariable("idItem");
        validate(productId, FormatUtils::isNumber, String.format("Invalid product id [ %s ]",productId) );
        return productStockReaderUseCase.getAllPurchaseItemsByPurchase(Integer.parseInt(productId))
                .collectList()
                .flatMap(p -> ServerResponse.ok().bodyValue(p));
    }*/


}
