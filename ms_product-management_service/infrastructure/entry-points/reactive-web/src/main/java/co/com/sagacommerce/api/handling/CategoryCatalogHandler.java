package co.com.sagacommerce.api.handling;

import co.com.sagacommerce.api.commons.FormatUtils;
import co.com.sagacommerce.usecase.stockreader.CategoryStockReaderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.sagacommerce.model.validation.ValidationService.validate;

@Component
@RequiredArgsConstructor
public class CategoryCatalogHandler {

    private final CategoryStockReaderUseCase categoryStockReaderUseCase;

    public Mono<ServerResponse> listenGETAllCategoriesUseCase(ServerRequest serverRequest) {
        return categoryStockReaderUseCase.getAllCategories()
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products));
    }


    public Mono<ServerResponse> listenGETCategoryUseCase(ServerRequest serverRequest) {
        var productId = serverRequest.pathVariable("id");
        validate(productId, FormatUtils::isNumber, String.format("Invalid category id [ %s ]",productId) );
        return categoryStockReaderUseCase.getCategory(Integer.parseInt(productId))
                .flatMap(p -> ServerResponse.ok().bodyValue(p));
    }
}
