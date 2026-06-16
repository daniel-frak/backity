package dev.codesoapbox.backity.core.filecopy.infrastructure.config.exceptionhandling;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.exceptionhandling.ErrorMessageHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.config.exceptionhandling.RestExceptionHandler;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ControllerTest
class FileCopyExceptionMessageKeyProviderIT {

    @Autowired
    private RestExceptionHandler restExceptionHandler;

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldRegisterMessageKeys() {
        var exception = new FileCopyNotBackedUpException(FileCopyId.newInstance());
        ResponseEntity<ErrorMessageHttpDto> result = restExceptionHandler.handleDomainErrors(exception);

        assertThat(result.getBody())
                .extracting(ErrorMessageHttpDto::messageKey)
                .isEqualTo("FILE_COPY_NOT_BACKED_UP");
    }
}