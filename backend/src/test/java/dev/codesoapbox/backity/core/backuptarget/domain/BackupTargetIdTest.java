package dev.codesoapbox.backity.core.backuptarget.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BackupTargetIdTest {

    @Test
    void shouldCreateFromString() {
        var result = new BackupTargetId("3553a3c7-47a7-4f7a-8b47-75928bee37d0");

        var expectedValue = UUID.fromString("3553a3c7-47a7-4f7a-8b47-75928bee37d0");
        assertThat(result.value()).isEqualTo(expectedValue);
    }

    @Test
    void shouldCreateNewInstance() {
        BackupTargetId result = BackupTargetId.newInstance();

        assertThat(result.value()).isNotNull();
    }

    @Test
    void toStringShouldReturnValue() {
        String idString = "3553a3c7-47a7-4f7a-8b47-75928bee37d0";
        var id = new BackupTargetId(idString);

        String result = id.toString();

        assertThat(result)
                .isEqualTo(idString);
    }
}