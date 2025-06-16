package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.gateways.Repository.PurchaseOrdersRepository;
import co.com.sagacommerce.model.validation.exceptions.TechnicalException;
import co.com.sagacommerce.r2dbch2sql.crud.PurchaseRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import static co.com.sagacommerce.model.validation.exceptions.message.TechnicalErrorMessage.DATABASE_TABLE_MODIFICATION_ERROR;

@Repository
@AllArgsConstructor
public class PurchaseOrdersUpdaterRepositoryImp implements PurchaseOrdersRepository {

    private final PurchaseRepositoryAdapter purchaseRepositoryAdapter;
    private final PurchaseItemUpdaterRepositoryImp purchaseItemUpdaterRepositoryImp;



    @Override
    public Mono<PurchaseDTO> addPurchaseOrder(PurchaseDTO purchaseDTO) {
        return purchaseRepositoryAdapter.save(purchaseDTO)
                .switchIfEmpty(Mono.error(() -> new TechnicalException(DATABASE_TABLE_MODIFICATION_ERROR)))
                .flatMap(savedPurchase ->
                        Flux.fromIterable(purchaseDTO.getItems())
                                .flatMap(item -> {
                                    item.setPurchaseId(savedPurchase.getId());
                                    return purchaseItemUpdaterRepositoryImp.addPurchaseItemOrder(item);
                                })
                                .then(Mono.just(savedPurchase))
                );
    }
}
