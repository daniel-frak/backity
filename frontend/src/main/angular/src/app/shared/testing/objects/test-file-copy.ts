import {FileCopyStatus, FileCopy} from "@backend";

export class TestFileCopy {

  public static tracked(): FileCopy {
    return {
      id: "someFileCopyId",
      naturalId: {
        gameFileId: "someGameFileId",
        backupTargetId: "someBackupTargetId"
      },
      status: FileCopyStatus.Tracked,
      failedReason: "someFailedReason",
      filePath: "someFilePath",
      dateCreated: "someDateCreated",
      dateModified: "someDateModified"
    };
  }

  public static enqueued(): FileCopy {
    return {
      ...TestFileCopy.tracked(),
      status: FileCopyStatus.Enqueued
    };
  }

  public static inProgress(): FileCopy {
    return {
      ...TestFileCopy.tracked(),
      status: FileCopyStatus.InProgress
    };
  }

  public static storedIntegrityUnknown(): FileCopy {
    return {
      ...TestFileCopy.tracked(),
      status: FileCopyStatus.StoredIntegrityUnknown
    };
  }
}
