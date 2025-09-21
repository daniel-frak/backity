import {FileCopyNaturalId, FileCopyReplicationProgressUpdatedEvent} from "@backend";

export namespace TestProgressUpdatedEvent {

  export function twentyFivePercent(fileCopyId: string, fileCopyNaturalId: FileCopyNaturalId
  ): FileCopyReplicationProgressUpdatedEvent {
    return {
      fileCopyId: fileCopyId,
      fileCopyNaturalId: fileCopyNaturalId,
      percentage: 25,
      timeLeftSeconds: 999
    };
  }
}
