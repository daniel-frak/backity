package dev.codesoapbox.backity.core.logs.application;

import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLogsUseCaseTest {

    private GetLogsUseCase useCase;

    @Mock
    private LogService logService;

    @BeforeEach
    void setUp() {
        useCase = new GetLogsUseCase(logService);
    }

    @Test
    void getLogs() {
        List<String> logs = singletonList("someLog");
        when(logService.getLogs())
                .thenReturn(logs);

        var result = useCase.getLogs();

        assertThat(result)
                .isEqualTo(logs);
    }
}