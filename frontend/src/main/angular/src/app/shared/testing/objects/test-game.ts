import {FileCopy, GameFile, GameWithFileCopies} from "@backend";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";

export class TestGame {

  public static withGameFileAndFileCopy(gameFile: GameFile, fileCopy: FileCopy): GameWithFileCopies {
    return {
      id: "someGameId",
      title: "someGameTitle",
      gameFilesWithCopies: [
        {
          gameFile: gameFile,
          fileCopies: [fileCopy]
        }
      ]
    };
  }

  public static withDiscoveredFileCopy(): GameWithFileCopies {
    return {
      id: "someGameId",
      title: "someGameTitle",
      gameFilesWithCopies: [
        {
          gameFile: TestGameFile.any(),
          fileCopies: [TestFileCopy.discovered()]
        }
      ]
    };
  }

  public static withSuccessfulFileCopy(): GameWithFileCopies {
    return {
      id: "someGameId",
      title: "someGameTitle",
      gameFilesWithCopies: [
        {
          gameFile: TestGameFile.any(),
          fileCopies: [TestFileCopy.successfullyProcessed()]
        }
      ]
    };
  }
}
