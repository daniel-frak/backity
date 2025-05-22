import {FileBackupStartedEvent, FileCopy, GameFile} from "@backend";

export class TestFileBackupStartedEvent {

  public static anEvent(): FileBackupStartedEvent {
    return {
      fileCopyId: "someFileCopyId",
      fileCopyNaturalId: {
        gameFileId: "someGameFileId",
        backupTargetId: "someBackupTargetId"
      },
      originalGameTitle: "Some current game",
      originalFileName: "Some original file name",
      version: "Some version",
      size: "3 GB",
      fileTitle: "currentGame.exe",
      filePath: "some/file/path"
    };
  }

  public static for(gameFile: GameFile, fileCopy: FileCopy): FileBackupStartedEvent {
    return {
      ...this.anEvent(),
      fileCopyNaturalId: {
        gameFileId: gameFile.id,
        backupTargetId: fileCopy.naturalId.backupTargetId
      },
      originalGameTitle: gameFile.fileSource.originalGameTitle,
      originalFileName: gameFile.fileSource.originalFileName,
      version: gameFile.fileSource.version,
      size: gameFile.fileSource.size,
      fileTitle: gameFile.fileSource.fileTitle,
      filePath: fileCopy.filePath
    };
  }
}
