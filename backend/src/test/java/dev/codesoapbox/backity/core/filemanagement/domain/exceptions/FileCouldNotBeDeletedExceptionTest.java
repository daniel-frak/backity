package dev.codesoapbox.backity.core.filemanagement.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileCouldNotBeDeletedExceptionTest {

    @Test
    void shouldCreateWithMessage() {
        var path = "somePath";
        var cause = new RuntimeException("test");

        var exception = new FileCouldNotBeDeletedException(path, cause);

        assertThat(exception)
                .hasMessage("File could not be deleted: somePath")
                .hasCause(cause);
    }
}