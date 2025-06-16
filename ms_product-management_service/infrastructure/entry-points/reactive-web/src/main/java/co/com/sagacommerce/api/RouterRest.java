package co.com.sagacommerce.api;

import co.com.sagacommerce.api.handling.CategoryCatalogHandler;
import co.com.sagacommerce.api.handling.ProductCatalogHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> productRouterFunction(ProductCatalogHandler productCatalogHandler) {
        return route(GET("/api/products/all"), productCatalogHandler::listenGETAllProductsUseCase)
                .and(route(GET("/api/product/{id}"), productCatalogHandler::listenGETProductUseCase))
                .and(route(GET("/api/products/category/{idCategory}"),
                        productCatalogHandler::listenGETProductsByCategoryUseCase));
               /* .and(route(GET("/api/items/purchase/{idItem}"),
                        productCatalogHandler::listenGETPurchaseItemUseCase));*/
    }

    @Bean
    public RouterFunction<ServerResponse> categoryRouterFunction(CategoryCatalogHandler categoryCatalogHandler) {
        return route(GET("/api/categories/all"), categoryCatalogHandler::listenGETAllCategoriesUseCase)
                .and(route(GET("/api/category/{id}"), categoryCatalogHandler::listenGETCategoryUseCase));
    }
}
