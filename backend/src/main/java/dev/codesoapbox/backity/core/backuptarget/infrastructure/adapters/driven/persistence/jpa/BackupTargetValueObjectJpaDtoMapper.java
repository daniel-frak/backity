package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetName;
import dev.codesoapbox.backity.core.backuptarget.domain.PathTemplate;

import java.util.UUID;

public class BackupTargetValueObjectJpaDtoMapper {

    public String getValue(BackupTargetId id) {
        return id.value().toString();
    }

    public BackupTargetId toBackupTargetId(UUID uuid) {
        return new BackupTargetId(uuid);
    }

    public String getValue(BackupTargetName name) {
        return name.value();
    }

    public BackupTargetName toBackupTargetName(String value) {
        return new BackupTargetName(value);
    }

    public String getValue(PathTemplate pathTemplate) {
        return pathTemplate.value();
    }

    public PathTemplate toPathTemplate(String value) {
        return new PathTemplate(value);
    }
}
