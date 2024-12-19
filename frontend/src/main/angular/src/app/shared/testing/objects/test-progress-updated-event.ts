import {FileBackupProgressUpdatedEvent} from "@backend";

export class TestProgressUpdatedEvent {

  public static twentyFivePercent(): FileBackupProgressUpdatedEvent {
    return {
      percentage: 25,
      timeLeftSeconds: 999
    };
  }
}
