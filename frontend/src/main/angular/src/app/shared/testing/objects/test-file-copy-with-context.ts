import {FileCopy, FileCopyWithContext} from "@backend";
import {TestSourceFile} from "@app/shared/testing/objects/test-source-file";

export namespace TestFileCopyWithContext {

  export function withFileCopy(fileCopy: FileCopy): FileCopyWithContext {
    return {
      fileCopy: fileCopy,
      sourceFile: TestSourceFile.any(),
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
