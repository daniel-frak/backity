package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "Page")
public record PageHttpDto<T>(
        List<T> content,
        int size,
        int totalPages,
        long totalElements,
        int pageSize,
        int pageNumber
) {
}
