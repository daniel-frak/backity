import {FileCopyNaturalId, FileCopyStatus, FileCopyStatusChangedEvent} from "@backend";

export class TestFileCopyStatusChangedEvent {

  public static with(fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId, newStatus: FileCopyStatus
  ): FileCopyStatusChangedEvent {
    return {
      fileCopyId: fileCopyId,
      fileCopyNaturalId: fileCopyNaturalId,
      newStatus: newStatus
    };
  }
}
