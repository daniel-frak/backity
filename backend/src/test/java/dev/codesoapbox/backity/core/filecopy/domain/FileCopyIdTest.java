package dev.codesoapbox.backity.core.filecopy.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyIdTest {

    @Test
    void shouldCreateFromString() {
        var result = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");

        var expectedValue = UUID.fromString("6df888e8-90b9-4df5-a237-0cba422c0310");
        assertThat(result.value()).isEqualTo(expectedValue);
    }

    @Test
    void shouldCreateNewInstance() {
        FileCopyId result = FileCopyId.newInstance();

        assertThat(result.value()).isNotNull();
    }

    @Test
    void toStringShouldReturnValue() {
        String idString = "6df888e8-90b9-4df5-a237-0cba422c0310";
        var id = new FileCopyId(idString);

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }
}