package dev.codesoapbox.backity.core.sourcefile.domain.exceptions;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceFileNotFoundExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new SourceFileId("3b21cc23-54c6-48f3-914d-188b790128b4");
        var exception = new SourceFileNotFoundException(id);

        String result = exception.getMessage();

        var expectedResult = "Could not find SourceFile with id=3b21cc23-54c6-48f3-914d-188b790128b4";
        assertThat(result).isEqualTo(expectedResult);
    }
}