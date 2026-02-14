package dev.codesoapbox.backity.core.backuptarget.domain.exceptions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidPathTemplatePlaceholdersExceptionTest {

    @Test
    void shouldGetMessage() {
        List<String> invalidPlaceholders = List.of("{INVALID_PLACEHOLDER_1}", "{INVALID_PLACEHOLDER_2}");
        var valueWithInvalidPlaceholder = "/test/{INVALID_PLACEHOLDER_1}/{INVALID_PLACEHOLDER_2}/{FILENAME}";
        var exception = new InvalidPathTemplatePlaceholdersException(invalidPlaceholders, valueWithInvalidPlaceholder);

        var result = exception.getMessage();

        assertThat(result).isEqualTo(
                "The PathTemplate ('/test/{INVALID_PLACEHOLDER_1}/{INVALID_PLACEHOLDER_2}/{FILENAME}')" +
                        " contains invalid placeholders ([{INVALID_PLACEHOLDER_1}, {INVALID_PLACEHOLDER_2}])");
    }
}