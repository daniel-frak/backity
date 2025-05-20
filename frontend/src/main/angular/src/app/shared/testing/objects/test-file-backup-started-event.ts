import {FileBackupStartedEvent, GameFile} from "@backend";

export class TestFileBackupStartedEvent {

  public static anEvent(): FileBackupStartedEvent {
    return {
      gameFileId: "someFileId",
      originalGameTitle: "Some current game",
      originalFileName: "Some original file name",
      version: "Some version",
      size: "3 GB",
      fileTitle: "currentGame.exe",
      filePath: "some/file/path"
    };
  }

  public static for(gameFile: GameFile): FileBackupStartedEvent {
    return {
      ...this.anEvent(),
      gameFileId: gameFile.id,
      originalGameTitle: gameFile.fileSource.originalGameTitle,
      originalFileName: gameFile.fileSource.originalFileName,
      version: gameFile.fileSource.version,
      size: gameFile.fileSource.size,
      fileTitle: gameFile.fileSource.fileTitle,
      filePath: gameFile.fileCopy.filePath
    };
  }
}
