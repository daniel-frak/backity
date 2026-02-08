package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileBackupContextFactoryTest {

    private FileBackupContextFactory fileBackupContextFactory;

    @Mock
    private SourceFileRepository sourceFileRepository;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @Mock
    private StorageSolutionRepository storageSolutionRepository;

    @BeforeEach
    void setUp() {
        fileBackupContextFactory = new FileBackupContextFactory(
                sourceFileRepository, backupTargetRepository, storageSolutionRepository);
    }

    @Test
    void shouldCreate() {
        FileBackupContext expectedResult = TestFileBackupContext.enqueuedLocalGog();
        exists(expectedResult.sourceFile());
        exists(expectedResult.backupTarget());
        exists(expectedResult.storageSolution());

        FileBackupContext result = fileBackupContextFactory.create(expectedResult.fileCopy());

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    private void exists(SourceFile sourceFile) {
        when(sourceFileRepository.getById(sourceFile.getId()))
                .thenReturn(sourceFile);
    }

    private void exists(BackupTarget backupTarget) {
        when(backupTargetRepository.getById(backupTarget.getId()))
                .thenReturn(backupTarget);
    }

    private void exists(StorageSolution storageSolution) {
        when(storageSolutionRepository.getById(storageSolution.getId()))
                .thenReturn(storageSolution);
    }
}