package co.com.sagacommerce.api.errorhandling;

import lombok.Builder;

@Builder
public record CustomError(String code, String detail) {
}
