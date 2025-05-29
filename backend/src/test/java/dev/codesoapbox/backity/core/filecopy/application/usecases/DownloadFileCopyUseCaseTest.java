package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
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
class DownloadFileCopyUseCaseTest {

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private StorageSolution storageSolution;

    private DownloadFileCopyUseCase downloadFileCopyUseCase;

    @BeforeEach
    void setUp() {
        downloadFileCopyUseCase = new DownloadFileCopyUseCase(fileCopyRepository, storageSolution);
    }

    @Test
    void shouldDownloadFileGivenFileCopyExistsAndFileCopyResourceExists() throws FileNotFoundException {
        FileCopy fileCopy = mockStoredUnverifiedFileCopyExists();
        FileResource fileResource = mockFileResourceExists(fileCopy);

        FileResource result = downloadFileCopyUseCase.downloadFileCopy(fileCopy.getId());

        assertThat(result).isEqualTo(fileResource);
    }

    private FileResource mockFileResourceExists(FileCopy fileCopy) throws FileNotFoundException {
        FileResource fileResource = new FileResource(mock(InputStream.class), 5120L, "test_file.exe");
        when(storageSolution.getFileResource(fileCopy.getFilePath()))
                .thenReturn(fileResource);

        return fileResource;
    }

    private FileCopy mockStoredUnverifiedFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);

        return fileCopy;
    }
}