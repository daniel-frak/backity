package dev.codesoapbox.backity.core.filedetails.domain.exceptions;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FileDetailsNotFoundExceptionTest {

    @Test
    void shouldGetMessage() {
        FileDetailsId id = new FileDetailsId(UUID.fromString("3b21cc23-54c6-48f3-914d-188b790128b4"));
        var exception = new FileDetailsNotFoundException(id);

        String result = exception.getMessage();

        var expectedResult = "Could not find FileDetails with id=3b21cc23-54c6-48f3-914d-188b790128b4";
        assertThat(result).isEqualTo(expectedResult);
    }
}