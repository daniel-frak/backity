import {FileCopyStatus, FileCopy} from "@backend";

export class TestFileCopy {

  public static discovered(): FileCopy {
    return {
      id: "someFileCopyId",
      naturalId: {
        gameFileId: "someGameFileId",
        backupTargetId: "someBackupTargetId"
      },
      status: FileCopyStatus.Discovered,
      failedReason: "someFailedReason",
      filePath: "someFilePath",
      dateCreated: "someDateCreated",
      dateModified: "someDateModified"
    };
  }

  public static enqueued(): FileCopy {
    return {
      ...this.discovered(),
      status: FileCopyStatus.Enqueued
    };
  }

  public static inProgress(): FileCopy {
    return {
      ...this.discovered(),
      status: FileCopyStatus.InProgress
    };
  }

  public static successfullyProcessed(): FileCopy {
    return {
      ...this.discovered(),
      status: FileCopyStatus.Success
    };
  }
}
