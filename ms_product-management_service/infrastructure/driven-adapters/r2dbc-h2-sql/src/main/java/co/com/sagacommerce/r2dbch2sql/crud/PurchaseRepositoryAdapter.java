package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.r2dbch2sql.entity.Purchase;
import co.com.sagacommerce.r2dbch2sql.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PurchaseRepositoryAdapter extends ReactiveAdapterOperations<
        PurchaseDTO,
        Purchase,
        Integer,
        PurchaseCrudRepository
        > {

    public PurchaseRepositoryAdapter(PurchaseCrudRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, PurchaseDTO.class));
    }

}
