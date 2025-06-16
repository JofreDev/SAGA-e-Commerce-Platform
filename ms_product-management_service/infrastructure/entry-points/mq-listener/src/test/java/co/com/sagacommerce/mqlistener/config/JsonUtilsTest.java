package co.com.sagacommerce.mqlistener.config;

import co.com.sagacommerce.model.validation.exceptions.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static co.com.sagacommerce.model.validation.exceptions.message.BusinessErrorMessage.VALIDATION_DATA_ERROR;
import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class SampleDTO {
        private String name;
        private int age;
    }

    @Test
    void shouldDeserializeValidJson() {
        String json = "{\"name\":\"Jofre\",\"age\":24}";

        JsonUtils.fromJson(json, SampleDTO.class)
                .as(StepVerifier::create)
                .assertNext(rta -> {
                    assertEquals("Jofre", rta.getName());
                    assertEquals(24, rta.getAge());
                }).verifyComplete();

    }

    @Test
    void shouldReturnErrorForInvalidJson() {
        String invalidJson = "{\"name\":\"Jofre\",\"age\":}";

        StepVerifier.create(JsonUtils.fromJson(invalidJson, SampleDTO.class))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BusinessException.class, throwable);
                    assertEquals(VALIDATION_DATA_ERROR.getMessage(),
                            ((BusinessException) throwable).getBusinessErrorMessage().getMessage());
                }).verify();


    }

}