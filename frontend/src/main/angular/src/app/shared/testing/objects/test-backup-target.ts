import {BackupTarget} from "@backend";

export namespace TestBackupTarget {

  export function localFolder(): BackupTarget {
    return {
      id: "localFolderBackupTargetId",
      storageSolutionId: "someStorageSolutionId",
      title: "Local folder backup target",
      pathTemplate: "somePathTemplate"
    };
  }

  export function s3(): BackupTarget {
    return {
      id: "s3BackupTargetId",
      storageSolutionId: "someStorageSolutionId",
      title: "Local folder backup target",
      pathTemplate: "somePathTemplate"
    };
  }
}
