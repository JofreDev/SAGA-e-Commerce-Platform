package co.com.sagacommerce.config;

import co.com.saga.commerce.model.gateways.PurchaseTransactionGateway;
import co.com.saga.commerce.model.gateways.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public CategoryStockReaderRepository categoryStockReaderRepository() {
            return mock(CategoryStockReaderRepository.class);
        }

        @Bean
        public ProductStockReaderRepository productStockReaderRepository() {
            return mock(ProductStockReaderRepository.class);
        }

        @Bean
        public ProductStockUpdaterRepository productStockUpdaterRepository() {
            return mock(ProductStockUpdaterRepository.class);
        }

        @Bean
        public PurchaseItemReaderRepository purchaseItemReaderRepository() {
            return mock(PurchaseItemReaderRepository.class);
        }

        @Bean
        public PurchaseOrdersReaderRepository purchaseOrdersReaderRepository() {
            return mock(PurchaseOrdersReaderRepository.class);
        }

        @Bean
        public PurchaseOrdersRepository purchaseOrdersRepository() {
            return mock(PurchaseOrdersRepository.class);
        }

        @Bean
        public PurchaseTransactionGateway purchaseTransactionGateway() {
            return mock(PurchaseTransactionGateway.class);
        }


    }


}