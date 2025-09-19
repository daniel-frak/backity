package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild",
        builderMethodName = "trackedLocalGogBuilder")
public class TestFileBackupContext {

    private static final FakeUnixStorageSolution DEFAULT_STORAGE_SOLUTION =
            new FakeUnixStorageSolution(5120);

    @lombok.Builder.Default
    FileCopy fileCopy = TestFileCopy.tracked();

    @lombok.Builder.Default
    GameFile gameFile = TestGameFile.gog();

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
            return new FileBackupContext(temp.fileCopy, temp.gameFile, temp.backupTarget, temp.storageSolution);
        }
    }
}