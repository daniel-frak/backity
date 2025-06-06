import {FileDownloadProgressUpdatedEvent} from "@backend";

export namespace TestProgressUpdatedEvent {

  export function twentyFivePercent(fileCopyId: string): FileDownloadProgressUpdatedEvent {
    return {
      fileCopyId: fileCopyId,
      percentage: 25,
      timeLeftSeconds: 999
    };
  }
}
