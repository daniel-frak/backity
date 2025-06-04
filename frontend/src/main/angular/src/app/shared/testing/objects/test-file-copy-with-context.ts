import {FileCopy, FileCopyWithContext} from "@backend";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";

export class TestFileCopyWithContext {

  public static withFileCopy(fileCopy: FileCopy): FileCopyWithContext {
    return {
      fileCopy: fileCopy,
      gameFile: {
        fileSource: TestGameFile.any().fileSource
      },
      game: {
        title: "Test Game"
      },
      backupTarget: {
        title: "Test backup target"
      }
    };
  }
}
