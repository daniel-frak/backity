package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild",
        builderMethodName = "trackedLocalGogBuilder")
public class TestFileBackupContext {

    private static final FakeUnixStorageSolution DEFAULT_STORAGE_SOLUTION =
            new FakeUnixStorageSolution();

    @lombok.Builder.Default
    FileCopy fileCopy = TestFileCopy.tracked();

    @lombok.Builder.Default
    SourceFile sourceFile = TestSourceFile.gog();

    @lombok.Builder.Default
    BackupTarget backupTarget = TestBackupTarget.localFolderBuilder()
            .withStorageSolutionId(DEFAULT_STORAGE_SOLUTION.getId())
            .build();

    @lombok.Builder.Default
    StorageSolution storageSolution = DEFAULT_STORAGE_SOLUTION;

    public static FileBackupContext trackedLocalGog() {
        return trackedLocalGogBuilder().build();
    }

    public static FileBackupContext enqueuedLocalGog() {
        return enqueuedLocalGogBuilder().build();
    }

    public static Builder enqueuedLocalGogBuilder() {
        return trackedLocalGogBuilder()
                .fileCopy(TestFileCopy.enqueued());
    }

    public static class Builder {

        public FileBackupContext build() {
            TestFileBackupContext temp = internalBuild();
            return new FileBackupContext(temp.fileCopy, temp.sourceFile, temp.backupTarget, temp.storageSolution);
        }
    }
}