package dev.codesoapbox.backity.core.gamefile.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@EqualsAndHashCode
public class FileSize {

    private static final Map<String, Long> UNIT_MAP = Map.ofEntries(
            Map.entry("B", 1L),
            Map.entry("KB", 1_024L),
            Map.entry("MB", 1_048_576L),
            Map.entry("GB", 1_073_741_824L),
            Map.entry("TB", 1_099_511_627_776L),
            Map.entry("PB", 1_125_899_906_842_624L)
    );
    private static final Pattern SIZE_PATTERN = Pattern.compile("(\\d*\\.?\\d+)\\s*(\\w+)");

    @Getter
    private final long bytes;

    public static FileSize fromString(String size) {
        validateNotNullOrEmpty(size);
        size = size.trim();
        Matcher matcher = getValidMatcher(size);
        double numericValue = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2);
        Long multiplier = getBytesMultiplier(unit);

        return new FileSize((long) (numericValue * multiplier));
    }

    private static Matcher getValidMatcher(String size) {
        Matcher matcher = SIZE_PATTERN.matcher(size);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid size string");
        }
        return matcher;
    }

    private static void validateNotNullOrEmpty(String size) {
        if (size == null || size.trim().isEmpty()) {
            throw new IllegalArgumentException("Size string cannot be null or empty");
        }
    }

    private static Long getBytesMultiplier(String unit) {
        Long multiplier = UNIT_MAP.get(unit);
        if (multiplier == null) {
            throw new IllegalArgumentException("Unknown unit in size string: " + unit);
        }
        return multiplier;
    }

    @Override
    public String toString() {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024L * 1024) {
            double sizeInKB = bytes / 1024.0;
            return formatSize(sizeInKB) + " KB";
        } else if (bytes < 1024L * 1024 * 1024) {
            double sizeInMB = bytes / (1024.0 * 1024);
            return formatSize(sizeInMB) + " MB";
        } else if (bytes < 1024L * 1024 * 1024 * 1024) {
            double sizeInGB = bytes / (1024.0 * 1024 * 1024);
            return formatSize(sizeInGB) + " GB";
        } else if (bytes < 1024L * 1024 * 1024 * 1024 * 1024) {
            double sizeInTB = bytes / (1024.0 * 1024 * 1024 * 1024);
            return formatSize(sizeInTB) + " TB";
        } else {
            double sizeInPB = bytes / (1024.0 * 1024 * 1024 * 1024 * 1024);
            return formatSize(sizeInPB) + " PB";
        }
    }

    private String formatSize(double size) {
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(size);
    }

    public FileSize add(FileSize other) {
        return new FileSize(bytes + other.getBytes());
    }
}
