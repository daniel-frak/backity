import {FileCopy, FileCopyStatus} from "@backend";

export namespace TestFileCopy {

  export function tracked(): FileCopy {
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

  export function enqueued(): FileCopy {
    return {
      ...TestFileCopy.tracked(),
      status: FileCopyStatus.Enqueued
    };
  }

  export function inProgress(): FileCopy {
    return {
      ...TestFileCopy.tracked(),
      status: FileCopyStatus.InProgress
    };
  }

  export function storedIntegrityUnknown(): FileCopy {
    return {
      ...TestFileCopy.tracked(),
      status: FileCopyStatus.StoredIntegrityUnknown
    };
  }
}
