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
      originalGameTitle: gameFile.gameProviderFile.originalGameTitle,
      originalFileName: gameFile.gameProviderFile.originalFileName,
      version: gameFile.gameProviderFile.version,
      size: gameFile.gameProviderFile.size,
      fileTitle: gameFile.gameProviderFile.fileTitle,
      filePath: gameFile.fileBackup.filePath
    };
  }
}
