import {GameWithFileCopies} from "@backend";
import {TestSourceFile} from "@app/shared/testing/objects/test-source-file";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";
import {TestProgress} from "@app/shared/testing/objects/test-progress";

export namespace TestGameWithFileCopies {

  export function withTrackedFileCopy(): GameWithFileCopies {
    return {
      id: "someGameId",
      title: "someGameTitle",
      sourceFilesWithCopies: [
        {
          sourceFile: TestSourceFile.any(),
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
      sourceFilesWithCopies: [
        {
          sourceFile: TestSourceFile.any(),
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
      sourceFilesWithCopies: [
        {
          sourceFile: TestSourceFile.any(),
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
