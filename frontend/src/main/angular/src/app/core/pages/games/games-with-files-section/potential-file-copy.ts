import {FileCopyStatus, FileCopy} from "@backend";

export type PotentialFileCopy = Omit<FileCopy, "id" | "status"> & { id?: string, status?: FileCopyStatus };

export class PotentialFileCopyFactory {

  static missing(gameFileId: string, backupTargetId: string): PotentialFileCopy {
    return {
      id: undefined,
      naturalId: {
        gameFileId: gameFileId,
        backupTargetId: backupTargetId
      },
      status: undefined
    };
  }
}
