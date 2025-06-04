import {FileDownloadProgressUpdatedEvent} from "@backend";

export namespace TestProgressUpdatedEvent {

  export function twentyFivePercent(): FileDownloadProgressUpdatedEvent {
    return {
      percentage: 25,
      timeLeftSeconds: 999
    };
  }
}
