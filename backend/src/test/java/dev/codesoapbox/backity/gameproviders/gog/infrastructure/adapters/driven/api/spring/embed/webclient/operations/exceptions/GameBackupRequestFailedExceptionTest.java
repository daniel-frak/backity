package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations.exceptions;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileUrl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameBackupRequestFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var fileUrl = new SourceFileUrl("/downlink/some_game/some_file");
        var exception = new GameBackupRequestFailedException(fileUrl, "someMessage");

        String result = exception.getMessage();

        assertThat(result)
                .isEqualTo(
                        "An error occurred while backing up file: /downlink/some_game/some_file. someMessage");
    }

    @Test
    void shouldGetCause() {
        var cause = new RuntimeException();
        var aFileUrl = new SourceFileUrl("/downlink/some_game/some_file");
        var exception = new GameBackupRequestFailedException(aFileUrl, cause);

        Throwable result = exception.getCause();

        assertThat(result).isSameAs(cause);
    }
}