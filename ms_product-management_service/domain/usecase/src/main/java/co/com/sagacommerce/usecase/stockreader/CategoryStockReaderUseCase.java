package co.com.sagacommerce.usecase.stockreader;

import co.com.sagacommerce.model.dto.CategoryDTO;
import co.com.sagacommerce.model.gateways.Repository.CategoryStockReaderRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CategoryStockReaderUseCase {

    private final CategoryStockReaderRepository categoryStockReaderRepository;

    public Mono<CategoryDTO> getCategory(int id) {
        return categoryStockReaderRepository.getCategory(id);
    }

    public Flux<CategoryDTO> getAllCategories() {
        return categoryStockReaderRepository.getAllCategories();
    }
}
