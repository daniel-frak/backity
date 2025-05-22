package dev.codesoapbox.backity.core.gamefile.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCurrentlyDownloadingFileUseCaseTest {

    private GetCurrentlyDownloadingFileUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private GameFileRepository gameFileRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetCurrentlyDownloadingFileUseCase(fileCopyRepository, gameFileRepository);
    }

    @Test
    void shouldFindCurrentlyDownloadingFile() {
        FileCopy fileCopy = TestFileCopy.inProgress();
        GameFile gameFile = TestGameFile.gog();
        when(fileCopyRepository.findCurrentlyDownloading())
                .thenReturn(Optional.of(fileCopy));
        when(gameFileRepository.getById(fileCopy.getGameFileId()))
                .thenReturn(gameFile);

        Optional<GameFile> result = useCase.findCurrentlyDownloadingFile();

        assertThat(result).isEqualTo(Optional.of(gameFile));
    }
}