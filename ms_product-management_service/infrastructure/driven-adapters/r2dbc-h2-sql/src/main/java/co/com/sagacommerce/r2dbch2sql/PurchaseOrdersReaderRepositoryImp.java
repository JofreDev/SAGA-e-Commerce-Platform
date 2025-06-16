package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.PurchaseDTO;
import co.com.sagacommerce.model.gateways.Repository.PurchaseItemReaderRepository;
import co.com.sagacommerce.model.gateways.Repository.PurchaseOrdersReaderRepository;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.model.validation.exceptions.TechnicalException;
import co.com.sagacommerce.r2dbch2sql.crud.PurchaseRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.RESOURCE_NOT_FOUND;

@Repository
@AllArgsConstructor
public class PurchaseOrdersReaderRepositoryImp implements PurchaseOrdersReaderRepository {

    private final PurchaseRepositoryAdapter purchaseRepositoryAdapter;
    private final PurchaseItemReaderRepository purchaseItemReaderRepository;

    @Override
    public Flux<PurchaseDTO> getAllPurchases() {
        return null;
    }

    @Override
    public Mono<PurchaseDTO> getPurchase(int purchaseId) {
        return purchaseRepositoryAdapter.findById(purchaseId)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(() -> new BusinessException(RESOURCE_NOT_FOUND,
                        String.format("Purchase id [ %S ] not found ", purchaseId))))
                .flatMap(p -> purchaseItemReaderRepository.
                        getItemsByPurchase(p.getId())
                        .collectList()
                        .map(itemsList -> {
                            p.setItems(itemsList);
                            return p;

                        })
                );
    }
}
