import {FileCopy, FileCopyStatus} from "@backend";

export type PotentialFileCopy = Omit<FileCopy, "id" | "status"> & { id?: string, status?: FileCopyStatus };

export class PotentialFileCopyFactory {

  static missing(sourceFileId: string, backupTargetId: string): PotentialFileCopy {
    return {
      id: undefined,
      naturalId: {
        sourceFileId: sourceFileId,
        backupTargetId: backupTargetId
      },
      status: undefined
    };
  }
}
