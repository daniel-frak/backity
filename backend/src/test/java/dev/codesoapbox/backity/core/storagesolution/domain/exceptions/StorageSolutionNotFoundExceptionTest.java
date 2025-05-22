package dev.codesoapbox.backity.core.storagesolution.domain.exceptions;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StorageSolutionNotFoundExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new StorageSolutionId("S3");
        var exception = new StorageSolutionNotFoundException(id);

        String result = exception.getMessage();

        var expectedResult = "Could not find StorageSolution with id=S3";
        assertThat(result).isEqualTo(expectedResult);
    }
}