import {GameFile, Progress} from "@backend";
import {PotentialFileCopy} from "@app/core/pages/games/games-with-files-card/potential-file-copy";

export interface PotentialFileCopyWithContext {
  gameFile: GameFile;
  potentialFileCopy: PotentialFileCopy;
  progress?: Progress
}
