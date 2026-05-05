package dev.codesoapbox.backity.core.storagesolution.domain.exceptions;

import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileCouldNotBeDeletedExceptionTest {

    @Test
    void shouldCreateWithMessage() {
        var path = new FilePath("somePath");
        var cause = new RuntimeException("test");

        var exception = new FileCouldNotBeDeletedException(path, cause);

        assertThat(exception)
                .hasMessage("File could not be deleted: somePath")
                .hasCause(cause);
    }
}