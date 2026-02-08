import {FileCopy, FileCopyWithContext} from "@backend";
import {TestGameFile} from "@app/shared/testing/objects/test-game-file";

export namespace TestFileCopyWithContext {

  export function withFileCopy(fileCopy: FileCopy): FileCopyWithContext {
    return {
      fileCopy: fileCopy,
      gameFile: TestGameFile.any(),
      game: {
        title: "Test Game"
      },
      backupTarget: {
        name: "Test backup target",
        storageSolutionId: "someStorageSolutionId"
      }
    };
  }
}
