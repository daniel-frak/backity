package dev.codesoapbox.backity.core.backuptarget.domain;

import java.util.List;

// @TODO Use this for GetBackupTargetsUseCase, map FileCopies to BackupTargets on the frontend
public interface BackupTargetRepository {

    List<BackupTarget> findAllBackupTargets();
}
