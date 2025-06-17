package co.com.sagacommerce.r2dbch2sql;

import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.saga.commerce.model.gateways.repository.PurchaseItemReaderRepository;
import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import co.com.sagacommerce.r2dbch2sql.crud.PurchaseItemRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.RESOURCE_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class PurchaseItemReaderRepositoryImp implements PurchaseItemReaderRepository {

    private final PurchaseItemRepositoryAdapter purchaseItemRepositoryAdapter;

    @Override
    public Flux<PurchaseItemDTO> getItemsByPurchase(int purchaseId) {
        return purchaseItemRepositoryAdapter.findAllByPurchaseId(purchaseId)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(() -> new BusinessException(RESOURCE_NOT_FOUND,
                        String.format("Item id [ %S ] not found ", purchaseId))));
    }
}
