package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.exceptionhandling;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValidationError")
public record ValidationErrorHttpDto(
        String fieldName,
        String message
) {
}
