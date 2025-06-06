import {FileCopyNaturalId, FileDownloadProgressUpdatedEvent} from "@backend";

export namespace TestProgressUpdatedEvent {

  export function twentyFivePercent(fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId
  ): FileDownloadProgressUpdatedEvent {
    return {
      fileCopyId: fileCopyId,
      fileCopyNaturalId: fileCopyNaturalId,
      percentage: 25,
      timeLeftSeconds: 999
    };
  }
}
