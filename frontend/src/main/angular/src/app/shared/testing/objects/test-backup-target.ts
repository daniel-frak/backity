import {BackupTarget} from "@backend";

export class TestBackupTarget {

  public static localFolder(): BackupTarget {
    return {
      id: "localFolderBackupTargetId",
      storageSolutionId: "someStorageSolutionId",
      title: "Local folder backup target",
      pathTemplate: "somePathTemplate"
    };
  }

  public static s3(): BackupTarget {
    return {
      id: "s3BackupTargetId",
      storageSolutionId: "someStorageSolutionId",
      title: "Local folder backup target",
      pathTemplate: "somePathTemplate"
    };
  }
}
