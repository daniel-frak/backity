package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.domain.TestPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProcessedFileListUseCaseTest {

    private GetProcessedFileListUseCase useCase;

    @Mock
    private GameFileRepository gameFileRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetProcessedFileListUseCase(gameFileRepository);
    }

    @Test
    void shouldGetProcessedFileList() {
        var pagination = new Pagination(0, 10);
        Page<GameFile> gameFilePage = mockGameFilePageExists(pagination);

        Page<GameFile> result = useCase.getProcessedFileList(pagination);

        assertThat(result).isEqualTo(gameFilePage);
    }

    private Page<GameFile> mockGameFilePageExists(Pagination pagination) {
        Page<GameFile> gameFilePage = TestPage.of(List.of(TestGameFile.successful()), pagination);
        when(gameFileRepository.findAllProcessed(pagination))
                .thenReturn(gameFilePage);

        return gameFilePage;
    }
}