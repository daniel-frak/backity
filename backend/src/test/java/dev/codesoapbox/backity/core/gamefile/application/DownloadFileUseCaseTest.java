package dev.codesoapbox.backity.core.gamefile.application;

import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DownloadFileUseCaseTest {

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private FileManager fileManager;

    private DownloadFileUseCase downloadFileUseCase;

    @BeforeEach
    void setUp() {
        downloadFileUseCase = new DownloadFileUseCase(gameFileRepository, fileManager);
    }

    @Test
    void shouldDownloadFileGivenGameFileExistsAndFileResourceExists() throws FileNotFoundException {
        GameFile gameFile = mockSuccessfulGameFileExists();
        FileResource fileResource = mockFileResourceExists(gameFile);

        FileResource result = downloadFileUseCase.downloadFile(gameFile.getId());

        assertThat(result).isEqualTo(fileResource);
    }

    private FileResource mockFileResourceExists(GameFile gameFile) throws FileNotFoundException {
        FileResource fileResource = new FileResource(mock(InputStream.class), 5120L, "test_file.exe");
        when(fileManager.getFileResource(gameFile.getFileBackup().getFilePath()))
                .thenReturn(fileResource);

        return fileResource;
    }

    private GameFile mockSuccessfulGameFileExists() {
        GameFile gameFile = TestGameFile.successful();
        when(gameFileRepository.getById(gameFile.getId()))
                .thenReturn(gameFile);

        return gameFile;
    }
}