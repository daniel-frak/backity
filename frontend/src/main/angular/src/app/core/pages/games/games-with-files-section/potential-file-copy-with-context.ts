import {BackupTarget, GameFile, Progress, StorageSolutionStatus} from "@backend";
import {PotentialFileCopy} from "@app/core/pages/games/games-with-files-section/potential-file-copy";

export interface PotentialFileCopyWithContext {
  gameFile: GameFile;
  potentialFileCopy: PotentialFileCopy;
  progress?: Progress,
  backupTarget: BackupTarget;
  storageSolutionStatus?: StorageSolutionStatus
}
