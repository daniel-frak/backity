import {FileBackupStatus, GameFile} from "@backend";

export class TestGameFile {

  public static discovered(): GameFile {
    return {
      id: "someFileId",
      gameId: "someGameId",
      fileSource: {
        gameProviderId: "someGameProviderId",
        originalGameTitle: "Some game",
        originalFileName: "Some original file name",
        version: "Some version",
        url: "some.url",
        size: "3 GB",
        fileTitle: "currentGame.exe"
      },
      fileCopy: {
        status: FileBackupStatus.Discovered
      }
    };
  }

  public static enqueued(): GameFile {
    return {
      ...this.discovered(),
      fileCopy: {
        status: FileBackupStatus.Enqueued
      }
    };
  }

  public static inProgress(): GameFile {
    return {
      ...this.discovered(),
      fileCopy: {
        status: FileBackupStatus.InProgress
      }
    };
  }

  public static successfullyProcessed(): GameFile {
    return {
      ...this.discovered(),
      fileCopy: {
        status: FileBackupStatus.Success
      }
    };
  }
}
