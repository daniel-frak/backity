import {FileBackupStatus, FileBackupStatusChangedEvent} from "@backend";

export class TestFileBackupStatusChangedEvent {

  public static with(id: string, newStatus: FileBackupStatus): FileBackupStatusChangedEvent {
    return {
      gameFileId: id,
      newStatus: newStatus
    };
  }
}
