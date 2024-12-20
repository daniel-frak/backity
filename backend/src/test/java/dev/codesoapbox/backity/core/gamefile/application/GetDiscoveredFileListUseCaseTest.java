package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDiscoveredFileListUseCaseTest {

    private GetDiscoveredFileListUseCase useCase;

    @Mock
    private GameFileRepository gameFileRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetDiscoveredFileListUseCase(gameFileRepository);
    }

    @Test
    void shouldGetDiscoveredFileList() {
        var pagination = new Pagination(0, 10);
        Page<GameFile> gameFilePage = mockGameFilePageExists(pagination);

        Page<GameFile> result = useCase.getDiscoveredFileList(pagination);

        assertThat(result).isEqualTo(gameFilePage);
    }

    private Page<GameFile> mockGameFilePageExists(Pagination pagination) {
        Page<GameFile> gameFile = new Page<>(List.of(TestGameFile.inProgress()),
                1, 1, 1, 10, 0);
        when(gameFileRepository.findAllDiscovered(pagination))
                .thenReturn(gameFile);

        return gameFile;
    }
}