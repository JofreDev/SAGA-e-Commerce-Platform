package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.sagacommerce.r2dbch2sql.entity.PurchaseItem;
import co.com.sagacommerce.r2dbch2sql.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public class PurchaseItemRepositoryAdapter extends ReactiveAdapterOperations<
        PurchaseItemDTO,
        PurchaseItem,
        Integer,
        PurchaseItemCrudRepository
        > {

    public PurchaseItemRepositoryAdapter(PurchaseItemCrudRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, PurchaseItemDTO.class));
    }

    public Flux<PurchaseItemDTO> findAllByPurchaseId(Integer purchaseId) {
        return repository.findAllByPurchaseId(purchaseId)
                .map(this::toEntity);
    }

    public Mono<Integer> deleteAllByPurchaseId(Integer purchaseId) {
        return repository.deleteAllByPurchaseId(purchaseId);
    }
}
