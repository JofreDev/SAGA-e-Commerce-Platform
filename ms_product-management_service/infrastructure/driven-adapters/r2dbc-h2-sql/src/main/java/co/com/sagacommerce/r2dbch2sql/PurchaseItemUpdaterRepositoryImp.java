package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.sagacommerce.model.validation.exceptions.TechnicalException;
import co.com.sagacommerce.r2dbch2sql.crud.PurchaseItemRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


import static co.com.sagacommerce.model.validation.exceptions.message.TechnicalErrorMessage.DATABASE_TABLE_MODIFICATION_ERROR;

@Repository
@RequiredArgsConstructor
public class PurchaseItemUpdaterRepositoryImp {

    private final PurchaseItemRepositoryAdapter purchaseItemRepositoryAdapter;


    public Mono<PurchaseItemDTO> addPurchaseItemOrder(PurchaseItemDTO purchaseItemDTO) {
        return purchaseItemRepositoryAdapter.save(purchaseItemDTO)
                .onErrorResume(error ->
                        Mono.error(new TechnicalException(DATABASE_TABLE_MODIFICATION_ERROR,error)))
                .doOnNext(item -> System.out.println("Item guardado " + item));
    }
}
