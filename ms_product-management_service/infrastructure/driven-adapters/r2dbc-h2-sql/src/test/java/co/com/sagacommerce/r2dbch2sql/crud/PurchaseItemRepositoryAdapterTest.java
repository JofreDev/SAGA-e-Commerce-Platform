package co.com.sagacommerce.r2dbch2sql.crud;

import co.com.sagacommerce.model.dto.PurchaseItemDTO;
import co.com.sagacommerce.r2dbch2sql.entity.PurchaseItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurchaseItemRepositoryAdapterTest {

    private PurchaseItemCrudRepository repository;
    private ObjectMapper objectMapper;
    private PurchaseItemRepositoryAdapter adapter;

    private PurchaseItem purchaseItem;
    private PurchaseItemDTO purchaseItemDTO;
    private Integer id;

    @BeforeEach
    void setUp() {
        repository = mock(PurchaseItemCrudRepository.class);
        objectMapper = mock(ObjectMapper.class);
        adapter = new PurchaseItemRepositoryAdapter(repository, objectMapper);

        id = 4;
        purchaseItem = new PurchaseItem(id, 1, 2,33, BigDecimal.valueOf(84.900), true);
        purchaseItemDTO = PurchaseItemDTO.builder()
                .id(id)
                .purchaseId(1)
                .productId(2)
                .quantity(3)
                .build();
    }

    @Test
    void testFindById() {
        when(repository.findById(id)).thenReturn(Mono.just(purchaseItem));
        when(objectMapper.map(purchaseItem, PurchaseItemDTO.class)).thenReturn(purchaseItemDTO);

        StepVerifier.create(adapter.findById(id))
                .expectNext(purchaseItemDTO)
                .verifyComplete();
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(Flux.just(purchaseItem));
        when(objectMapper.map(purchaseItem, PurchaseItemDTO.class)).thenReturn(purchaseItemDTO);

        StepVerifier.create(adapter.findAll())
                .expectNext(purchaseItemDTO)
                .verifyComplete();
    }

    @Test
    void testSave() {
        when(objectMapper.map(purchaseItemDTO, PurchaseItem.class)).thenReturn(purchaseItem);
        when(repository.save(purchaseItem)).thenReturn(Mono.just(purchaseItem));
        when(objectMapper.map(purchaseItem, PurchaseItemDTO.class)).thenReturn(purchaseItemDTO);

        StepVerifier.create(adapter.save(purchaseItemDTO))
                .expectNext(purchaseItemDTO)
                .verifyComplete();
    }

    @Test
    void testFindAllByPurchaseId() {
        when(repository.findAllByPurchaseId(1)).thenReturn(Flux.just(purchaseItem));
        when(objectMapper.map(purchaseItem, PurchaseItemDTO.class)).thenReturn(purchaseItemDTO);

        StepVerifier.create(adapter.findAllByPurchaseId(1))
                .expectNext(purchaseItemDTO)
                .verifyComplete();
    }

}