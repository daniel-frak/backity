package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
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
    private BackupTargetRepository backupTargetRepository;

    @Mock
    private StorageSolutionRepository storageSolutionRepository;

    private DownloadFileCopyUseCase downloadFileCopyUseCase;

    @BeforeEach
    void setUp() {
        downloadFileCopyUseCase = new DownloadFileCopyUseCase(
                fileCopyRepository, backupTargetRepository, storageSolutionRepository);
    }

    @Test
    void shouldDownloadFileGivenFileCopyExistsAndFileCopyResourceExists() throws FileNotFoundException {
        FileCopy fileCopy = mockStoredUnverifiedFileCopyExists();
        StorageSolution storageSolution = mockStorageSolutionExists(fileCopy);
        FileResource fileResource = mockFileResourceExists(fileCopy, storageSolution);

        FileResource result = downloadFileCopyUseCase.downloadFileCopy(fileCopy.getId());

        assertThat(result).isEqualTo(fileResource);
    }

    private FileCopy mockStoredUnverifiedFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.storedIntegrityUnknown();
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);

        return fileCopy;
    }

    private StorageSolution mockStorageSolutionExists(FileCopy fileCopy) {
        BackupTarget backupTarget = mockBackupTargetExists(fileCopy);
        return mockStorageSolutionExists(backupTarget);
    }

    private StorageSolution mockStorageSolutionExists(BackupTarget backupTarget) {
        StorageSolution storageSolution = mock(StorageSolution.class);
        when(storageSolutionRepository.getById(backupTarget.getStorageSolutionId()))
                .thenReturn(storageSolution);
        return storageSolution;
    }

    private BackupTarget mockBackupTargetExists(FileCopy fileCopy) {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        when(backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId()))
                .thenReturn(backupTarget);
        return backupTarget;
    }

    private FileResource mockFileResourceExists(FileCopy fileCopy, StorageSolution storageSolution)
            throws FileNotFoundException {
        FileResource fileResource = new FileResource(mock(InputStream.class), 5120L, "test_file.exe");
        when(storageSolution.getFileResource(fileCopy.getFilePath()))
                .thenReturn(fileResource);

        return fileResource;
    }
}