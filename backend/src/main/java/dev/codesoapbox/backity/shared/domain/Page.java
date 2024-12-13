package dev.codesoapbox.backity.shared.domain;

import java.util.List;
import java.util.function.Function;

public record Page<T>(
        List<T> content,
        int size,
        int totalPages,
        long totalElements,
        int pageSize,
        int pageNumber
) {
    public <U> Page<U> map(Function<T, U> contentMapper) {
        List<U> mappedContent = content.stream()
                .map(contentMapper)
                .toList();
        return new Page<>(mappedContent, size, totalPages, totalElements, pageSize, pageNumber);
    }
}
