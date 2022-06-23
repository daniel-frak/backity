package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.downloading.domain.exceptions.UnrecognizedFileSizeUnitException;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class FileSizeAccumulator {

    private static final BigDecimal KB_IN_B = BigDecimal.valueOf(1_000);
    private static final BigDecimal MB_IN_B = BigDecimal.valueOf(1_000_000);
    private static final BigDecimal GB_IN_B = BigDecimal.valueOf(1_000_000_000);
    private static final BigDecimal TB_IN_B = BigDecimal.valueOf(1_000_000_000_000L);
    private static final String SPACE = " ";

    private final AtomicReference<BigDecimal> totalSizeInBytes = new AtomicReference<>(BigDecimal.ZERO);

    public synchronized FileSizeAccumulator add(String fileSize) {
        String[] parts = fileSize.split(SPACE);
        BigDecimal size = convertToBytes(Double.parseDouble(parts[0]), parts[1]);
        totalSizeInBytes.accumulateAndGet(size, BigDecimal::add);

        return this;
    }

    private BigDecimal convertToBytes(double originalSize, String originalFormat) {
        if (originalFormat.equals("B")) {
            return BigDecimal.valueOf(originalSize);
        }
        if (originalFormat.equals("KB")) {
            return BigDecimal.valueOf(originalSize).multiply(KB_IN_B);
        }
        if (originalFormat.equals("MB")) {
            return BigDecimal.valueOf(originalSize).multiply(MB_IN_B);
        }
        if (originalFormat.equals("GB")) {
            return BigDecimal.valueOf(originalSize).multiply(GB_IN_B);
        }
        if (originalFormat.equals("TB")) {
            return BigDecimal.valueOf(originalSize).multiply(TB_IN_B);
        }

        throw new UnrecognizedFileSizeUnitException(originalFormat);
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
