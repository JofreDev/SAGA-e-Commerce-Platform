package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.model.dto.CategoryDTO;
import co.com.sagacommerce.r2dbch2sql.entity.Category;
import co.com.sagacommerce.r2dbch2sql.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;


@Repository
public class CategoryRepositoryAdapter extends ReactiveAdapterOperations<
        CategoryDTO,
        Category,
        Integer,
        CategoryCrudRepository
        > {

    public CategoryRepositoryAdapter(CategoryCrudRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, CategoryDTO.class));
    }


}
