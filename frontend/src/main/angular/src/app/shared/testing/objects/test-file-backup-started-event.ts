import {FileBackupStartedEvent, FileCopy, GameFile} from "@backend";
import {TestFileCopy} from "@app/shared/testing/objects/test-file-copy";

export class TestFileBackupStartedEvent {

  public static anEvent(): FileBackupStartedEvent {
    return {
      fileCopyWithContext: {
        fileCopy: TestFileCopy.inProgress(),
        gameFile: {
          fileSource: {
            gameProviderId: "someGameProviderId",
            originalGameTitle: "Some game",
            originalFileName: "Some original file name",
            version: "Some version",
            url: "some.url",
            size: "3 GB",
            fileTitle: "currentGame.exe"
          }
        },
        game: {
          title: "Test game"
        }
      }
    };
  }

  public static for(gameFile: GameFile, fileCopy: FileCopy): FileBackupStartedEvent {
    const originalEvent: FileBackupStartedEvent = this.anEvent();
    return {
      ...originalEvent,
      fileCopyWithContext: {
        ...originalEvent.fileCopyWithContext,
        fileCopy: fileCopy,
        gameFile: {
          fileSource: gameFile.fileSource
        }
      }
    };
  }
}
