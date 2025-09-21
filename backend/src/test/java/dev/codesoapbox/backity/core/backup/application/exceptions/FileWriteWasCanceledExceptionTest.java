package dev.codesoapbox.backity.core.backup.application.exceptions;

import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileWriteWasCanceledExceptionTest {

    @Test
    void shouldGetMessage() {
        var storageSolution = new FakeUnixStorageSolution();
        var exception = new FileWriteWasCanceledException("test", storageSolution);

        String result = exception.getMessage();

        assertThat(result).isEqualTo("File write into FakeUnixStorageSolution was canceled for 'test'");
    }
}