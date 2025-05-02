package dev.codesoapbox.backity.core.gamefile.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"LombokGetterMayBeUsed", "ClassCanBeRecord"})
@RequiredArgsConstructor
@EqualsAndHashCode
public class FileSize {

    private static final String[] UNITS = new String[]{ "B", "KB", "MB", "GB", "TB", "PB" };
    private static final Map<String, Long> UNIT_MAP = Map.ofEntries(
            Map.entry("B", 1L),
            Map.entry("KB", 1_024L),
            Map.entry("MB", 1_048_576L),
            Map.entry("GB", 1_073_741_824L),
            Map.entry("TB", 1_099_511_627_776L),
            Map.entry("PB", 1_125_899_906_842_624L)
    );
    private static final Pattern SIZE_PATTERN = Pattern.compile("^(\\d++\\.\\d++|\\d++)\\s*+(\\w++)$",
            Pattern.UNICODE_CHARACTER_CLASS);
    private static final int NUMERIC_VALUE_REGEX_GROUP = 1;
    private static final int UNIT_REGEX_GROUP = 2;
    private static final int BYTES_IN_KILOBYTE = 1024;

    @Getter
    private final long bytes;

    public static FileSize fromString(String size) {
        validateNotNullOrEmpty(size);
        size = size.trim();
        Matcher matcher = getValidMatcher(size);
        double numericValue = Double.parseDouble(matcher.group(NUMERIC_VALUE_REGEX_GROUP));
        String unit = matcher.group(UNIT_REGEX_GROUP);
        Long multiplier = getBytesMultiplier(unit);

        return new FileSize((long) (numericValue * multiplier));
    }

    private static void validateNotNullOrEmpty(String size) {
        if (size == null || size.trim().isEmpty()) {
            throw new IllegalArgumentException("Size string cannot be null or empty");
        }
    }

    private static Matcher getValidMatcher(String size) {
        Matcher matcher = SIZE_PATTERN.matcher(size);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid size string");
        }
        return matcher;
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
        double size = bytes;
        int unitIndex = 0;

        while (size >= BYTES_IN_KILOBYTE && unitIndex < UNITS.length - NUMERIC_VALUE_REGEX_GROUP) {
            size /= BYTES_IN_KILOBYTE;
            unitIndex++;
        }

        return formatSize(size) + " " + UNITS[unitIndex];
    }

    private String formatSize(double size) {
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(size);
    }

    public FileSize add(FileSize other) {
        return new FileSize(bytes + other.getBytes());
    }
}
