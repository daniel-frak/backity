package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.enqueuedGameFile;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEnqueuedFileListUseCaseTest {

    private GetEnqueuedFileListUseCase useCase;

    @Mock
    private GameFileRepository gameFileRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetEnqueuedFileListUseCase(gameFileRepository);
    }

    @Test
    void shouldGetEnqueuedFileList() {
        Pagination pagination = new Pagination(0, 10);
        Page<GameFile> gameFilePage = new Page<>(singletonList(enqueuedGameFile().build()),
                1, 1, 1, 10, 0);
        when(gameFileRepository.findAllWaitingForDownload(pagination))
                .thenReturn(gameFilePage);

        Page<GameFile> result = useCase.getEnqueuedFileList(pagination);

        assertThat(result).isEqualTo(gameFilePage);
    }
}