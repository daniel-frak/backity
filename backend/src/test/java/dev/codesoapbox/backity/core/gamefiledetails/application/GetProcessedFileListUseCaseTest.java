package dev.codesoapbox.backity.core.gamefiledetails.application;

import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.successfulFileDetails;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProcessedFileListUseCaseTest {

    private GetProcessedFileListUseCase useCase;

    @Mock
    private GameFileDetailsRepository gameFileDetailsRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetProcessedFileListUseCase(gameFileDetailsRepository);
    }

    @Test
    void shouldGetProcessedFileList() {
        Pagination pagination = new Pagination(0, 10);
        Page<GameFileDetails> gameFileDetails = new Page<>(singletonList(successfulFileDetails().build()),
                1, 1, 1, 10, 0);
        when(gameFileDetailsRepository.findAllProcessed(pagination))
                .thenReturn(gameFileDetails);

        Page<GameFileDetails> result = useCase.getProcessedFileList(pagination);

        assertThat(result).isEqualTo(gameFileDetails);
    }
}