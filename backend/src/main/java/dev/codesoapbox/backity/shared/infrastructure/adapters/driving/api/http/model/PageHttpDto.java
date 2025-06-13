package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "Page")
public record PageHttpDto<T>(
        @NotNull List<T> content,
        @NotNull int size,
        @NotNull int totalPages,
        @NotNull long totalElements,
        @NotNull int pageSize,
        @NotNull int pageNumber
) {
}
