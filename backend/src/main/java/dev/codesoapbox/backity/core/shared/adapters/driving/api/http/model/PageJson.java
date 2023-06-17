package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model;

import java.util.List;

/**
This class doesn't have {@code @Schema(name="Page")} on it because it would break generics in OpenAPI.
 */
public record PageJson<T>(
        List<T> content,
        int size,
        int totalPages,
        long totalElements,
        int pageSize,
        int pageNumber
) {
}
