import {BackupTarget, Progress, SourceFile, StorageSolutionStatus} from "@backend";
import {PotentialFileCopy} from "@app/core/pages/games/games-with-files-section/potential-file-copy";

export interface PotentialFileCopyWithContext {
  sourceFile: SourceFile;
  potentialFileCopy: PotentialFileCopy;
  progress?: Progress,
  backupTarget: BackupTarget;
  storageSolutionStatus?: StorageSolutionStatus
}
