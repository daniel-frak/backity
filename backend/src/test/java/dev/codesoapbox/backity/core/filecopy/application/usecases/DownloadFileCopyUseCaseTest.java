package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
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
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        FileCopy fileCopy = TestFileCopy.storedIntegrityUnknownBuilder()
                .naturalId(aFileCopyNaturalId(backupTarget.getId()))
                .build();
        FileResource fileResource = aFileResource();
        StorageSolution storageSolution = aStorageSolution(backupTarget, fileCopy, fileResource);
        exists(fileCopy);
        exists(backupTarget);
        exists(storageSolution);

        FileResource result = downloadFileCopyUseCase.execute(fileCopy.getId());

        assertThat(result).isEqualTo(fileResource);
    }

    private FileCopyNaturalId aFileCopyNaturalId(BackupTargetId backupTargetId) {
        return new FileCopyNaturalId(
                new SourceFileId("c7384581-b74e-4df4-b6e9-04046c9afca6"),
                backupTargetId
        );
    }

    private FileResource aFileResource() {
        return new FileResource(mock(InputStream.class), 5120L, "test_file.exe");
    }

    private StorageSolution aStorageSolution(BackupTarget backupTarget, FileCopy fileCopy, FileResource fileResource)
            throws FileNotFoundException {
        StorageSolution storageSolution = mock(StorageSolution.class);
        when(storageSolution.getFileResource(fileCopy.getFilePath()))
                .thenReturn(fileResource);
        when(storageSolution.getId())
                .thenReturn(backupTarget.getStorageSolutionId());

        return storageSolution;
    }

    private void exists(FileCopy fileCopy) {
        when(fileCopyRepository.getById(fileCopy.getId()))
                .thenReturn(fileCopy);
    }

    private void exists(StorageSolution storageSolution) {
        when(storageSolutionRepository.getById(storageSolution.getId()))
                .thenReturn(storageSolution);
    }

    private void exists(BackupTarget backupTarget) {
        when(backupTargetRepository.getById(backupTarget.getId()))
                .thenReturn(backupTarget);
    }
}