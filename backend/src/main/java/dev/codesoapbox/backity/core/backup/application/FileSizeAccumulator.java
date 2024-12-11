package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.exceptions.UnrecognizedFileSizeUnitException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FileSizeAccumulator {

    private static final Map<String, BigDecimal> MULTIPLIERS_BY_SIZE_FORMAT = Map.of(
            "B", BigDecimal.ONE,
            "KB", BigDecimal.valueOf(1_000),
            "MB", BigDecimal.valueOf(1_000_000),
            "GB", BigDecimal.valueOf(1_000_000_000),
            "TB", BigDecimal.valueOf(1_000_000_000_000L)
    );
    private static final String SPACE = " ";

    private final AtomicReference<BigDecimal> totalSizeInBytes = new AtomicReference<>(BigDecimal.ZERO);

    public synchronized FileSizeAccumulator add(String fileSize) {
        String[] parts = fileSize.split(SPACE);
        BigDecimal size = convertToBytes(Double.parseDouble(parts[0]), parts[1]);
        totalSizeInBytes.accumulateAndGet(size, BigDecimal::add);

        return this;
    }

    private BigDecimal convertToBytes(double originalSize, String originalFormat) {
        if (!MULTIPLIERS_BY_SIZE_FORMAT.containsKey(originalFormat)) {
            throw new UnrecognizedFileSizeUnitException(originalFormat);
        }
        BigDecimal multiplier = MULTIPLIERS_BY_SIZE_FORMAT.get(originalFormat);
        return BigDecimal.valueOf(originalSize).multiply(multiplier);
    }

    public Long getInBytes() {
        return totalSizeInBytes.get().longValue();
    }

    @Override
    public String toString() {
        return totalSizeInBytes.get().longValue()
                + " bytes";
    }
}
