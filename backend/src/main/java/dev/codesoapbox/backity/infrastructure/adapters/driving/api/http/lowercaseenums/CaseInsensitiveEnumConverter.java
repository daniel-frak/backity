package dev.codesoapbox.backity.infrastructure.adapters.driving.api.http.lowercaseenums;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

/**
 * Converts Strings to Enums, ignoring case. Assumes Enum values are uppercase in code.
 */
@RequiredArgsConstructor
public class CaseInsensitiveEnumConverter<T extends Enum<T>> implements Converter<String, T> {

    private final Class<T> enumClass;

    @Override
    public T convert(String source) {
        return T.valueOf(enumClass, source.toUpperCase(Locale.ENGLISH));
    }
}
