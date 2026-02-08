package dev.codesoapbox.backity.core.sourcefile.domain.exceptions;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiscoveredFileUrlEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        var id = new GameProviderId("TestGameProviderId");
        var exception = new DiscoveredFileUrlEmptyException(id);

        String result = exception.getMessage();

        var expectedResult = "Url was empty for Discovered File with Game Provider id: TestGameProviderId";
        assertThat(result).isEqualTo(expectedResult);
    }
}