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

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.enqueuedFileDetails;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEnqueuedFileListUseCaseTest {

    private GetEnqueuedFileListUseCase useCase;

    @Mock
    private GameFileDetailsRepository gameFileDetailsRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetEnqueuedFileListUseCase(gameFileDetailsRepository);
    }

    @Test
    void shouldGetEnqueuedFileList() {
        Pagination pagination = new Pagination(0, 10);
        Page<GameFileDetails> gameFileDetails = new Page<>(singletonList(enqueuedFileDetails().build()),
                1, 1, 1, 10, 0);
        when(gameFileDetailsRepository.findAllWaitingForDownload(pagination))
                .thenReturn(gameFileDetails);

        Page<GameFileDetails> result = useCase.getEnqueuedFileList(pagination);

        assertThat(result).isEqualTo(gameFileDetails);
    }
}