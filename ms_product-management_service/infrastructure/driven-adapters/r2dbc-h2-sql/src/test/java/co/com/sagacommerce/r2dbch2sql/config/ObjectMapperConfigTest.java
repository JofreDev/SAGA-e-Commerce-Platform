package co.com.sagacommerce.r2dbch2sql.config;

import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ObjectMapperConfig.class)
class ObjectMapperConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void objectMapperBeanShouldBeCreated() {
        assertNotNull(objectMapper, "Should be initialized");
        assertInstanceOf(ObjectMapper.class,objectMapper);
    }
}