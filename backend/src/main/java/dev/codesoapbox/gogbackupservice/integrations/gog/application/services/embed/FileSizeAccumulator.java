package dev.codesoapbox.gogbackupservice.integrations.gog.application.services.embed;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class FileSizeAccumulator {

    private static final BigDecimal KB_IN_B = BigDecimal.valueOf(1_000);
    private static final BigDecimal MB_IN_B = BigDecimal.valueOf(1_000_000);
    private static final BigDecimal GB_IN_B = BigDecimal.valueOf(1_000_000_000);
    private static final BigDecimal TB_IN_B = BigDecimal.valueOf(1_000_000_000_000L);

    private final AtomicReference<BigDecimal> totalSizeInBytes = new AtomicReference<>(BigDecimal.ZERO);

    public synchronized FileSizeAccumulator add(String fileSize) {
        String[] parts = fileSize.split(" ");
        BigDecimal size = convertSize(Double.parseDouble(parts[0]), parts[1]);
        totalSizeInBytes.accumulateAndGet(size, BigDecimal::add);

        return this;
    }

    private BigDecimal convertSize(double originalSize, String originalFormat) {
        if(originalFormat.equals("KB")) {
            return BigDecimal.valueOf(originalSize).multiply(KB_IN_B);
        }
        if(originalFormat.equals("MB")) {
            return BigDecimal.valueOf(originalSize).multiply(MB_IN_B);
        }
        if(originalFormat.equals("GB")) {
            return BigDecimal.valueOf(originalSize).multiply(GB_IN_B);
        }
        if(originalFormat.equals("TB")) {
            return BigDecimal.valueOf(originalSize).multiply(TB_IN_B);
        }
        return BigDecimal.valueOf(originalSize);
    }

    public Long getInBytes() {
        return totalSizeInBytes.get().longValue();
    }

    @Override
    public String toString() {
        return totalSizeInBytes.get()
                + " bytes";
    }
}