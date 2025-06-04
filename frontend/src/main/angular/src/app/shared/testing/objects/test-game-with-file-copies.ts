import {GameWithFileCopies} from "@backend";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";

export namespace TestGameWithFileCopies {

  export function withTrackedFileCopy(): GameWithFileCopies {
    return {
      id: "someGameId",
      title: "someGameTitle",
      gameFilesWithCopies: [
        {
          gameFile: TestGameFile.any(),
          fileCopies: [TestFileCopy.tracked()]
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
          fileCopies: [TestFileCopy.inProgress()]
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
          fileCopies: [TestFileCopy.storedIntegrityUnknown()]
        }
      ]
    };
  }
}
