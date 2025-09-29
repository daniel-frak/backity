package dev.codesoapbox.backity.core.backup.application.exceptions;

import dev.codesoapbox.backity.core.backup.application.WriteDestination;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentFileWriteExceptionTest {

    @Test
    void shouldGetMessage() {
        var writeDestination = new WriteDestination(new StorageSolutionId("STORAGE_SOLUTION_ID"),
                "testPath");
        var exception = new ConcurrentFileWriteException(writeDestination);

        assertThat(exception)
                .hasMessage("File 'testPath' in STORAGE_SOLUTION_ID is currently being written to by another thread");
    }
}