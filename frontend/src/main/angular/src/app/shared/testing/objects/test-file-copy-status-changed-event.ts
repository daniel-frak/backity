import {FileCopyNaturalId, FileCopyStatus, FileCopyStatusChangedEvent} from "@backend";

export namespace TestFileCopyStatusChangedEvent {

  export function withContent(fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId, newStatus: FileCopyStatus
  ): FileCopyStatusChangedEvent {
    return {
      fileCopyId: fileCopyId,
      fileCopyNaturalId: fileCopyNaturalId,
      newStatus: newStatus
    };
  }
}
