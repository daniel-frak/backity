package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "Pagination")
public record PaginationJson(
        @PositiveOrZero
        Integer page,
        @Positive
        @Schema(example = "20")
        Integer size
) {
}
