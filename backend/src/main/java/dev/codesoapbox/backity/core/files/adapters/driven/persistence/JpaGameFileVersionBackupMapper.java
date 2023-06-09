package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import org.mapstruct.Mapper;

@Mapper
public abstract class JpaGameFileVersionBackupMapper {

    public abstract JpaGameFileVersionBackup toEntity(GameFileVersionBackup model);

    public abstract GameFileVersionBackup toModel(JpaGameFileVersionBackup entity);
}
