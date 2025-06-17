package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.CategoryDTO;
import co.com.saga.commerce.model.gateways.repository.CategoryStockReaderRepository;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.r2dbch2sql.crud.CategoryRepositoryAdapter;
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
public class CategoryStockReaderRepositoryImp implements CategoryStockReaderRepository {

    private final CategoryRepositoryAdapter categoryRepositoryAdapter;

    @Override
    public Mono<CategoryDTO> getCategory(int categoryId) {
        return categoryRepositoryAdapter.findById(categoryId)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(() -> new BusinessException(RESOURCE_NOT_FOUND,
                        String.format("Category id [ %S ] not found ", categoryId)))
                );
    }

    @Override
    public Flux<CategoryDTO> getAllCategories() {
        return categoryRepositoryAdapter.findAll();
    }
}
