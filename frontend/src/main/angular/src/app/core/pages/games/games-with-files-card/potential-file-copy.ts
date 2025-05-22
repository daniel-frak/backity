import {FileCopyStatus, FileCopy} from "@backend";

export type PotentialFileCopy = Omit<FileCopy, "id"> & { id?: string };

export class PotentialFileCopyFactory {

  static missing(gameFileId: string, backupTargetId: string): PotentialFileCopy {
    return {
      id: undefined,
      naturalId: {
        gameFileId: gameFileId,
        backupTargetId: backupTargetId
      },
      status: FileCopyStatus.Discovered
    };
  }
}
