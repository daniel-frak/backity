import {GameWithFileCopies} from "@backend";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";
import {TestProgress} from "@app/shared/testing/objects/test-progress";

export namespace TestGameWithFileCopies {

  export function withTrackedFileCopy(): GameWithFileCopies {
    return {
      id: "someGameId",
      title: "someGameTitle",
      gameFilesWithCopies: [
        {
          gameFile: TestGameFile.any(),
          fileCopiesWithProgress: [
            {
              fileCopy: TestFileCopy.tracked(),
              progress: undefined
            }
          ]
        }
      ]
    };
  }

  export function withInProgressFileCopy(): GameWithFileCopies {
    return {
      id: "someGameId",
      title: "someGameTitle",
      gameFilesWithCopies: [
        {
          gameFile: TestGameFile.any(),
          fileCopiesWithProgress: [
            {
              fileCopy: TestFileCopy.inProgress(),
              progress: TestProgress.twentyFivePercent()
            }
          ]
        }
      ]
    };
  }

  export function withStoredUnverifiedFileCopy(): GameWithFileCopies {
    return {
      id: "someGameId",
      title: "someGameTitle",
      gameFilesWithCopies: [
        {
          gameFile: TestGameFile.any(),
          fileCopiesWithProgress: [
            {
              fileCopy: TestFileCopy.storedIntegrityUnknown(),
              progress: undefined
            }
          ]
        }
      ]
    };
  }
}
