import {FileDownloadProgressUpdatedEvent} from "@backend";

export class TestProgressUpdatedEvent {

  public static twentyFivePercent(): FileDownloadProgressUpdatedEvent {
    return {
      percentage: 25,
      timeLeftSeconds: 999
    };
  }
}
