package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetName;
import dev.codesoapbox.backity.core.backuptarget.domain.PathTemplate;

public class BackupTargetValueObjectWsDtoMapper {

    public String getValue(BackupTargetId id) {
        return id.value().toString();
    }

    public String getValue(BackupTargetName name) {
        return name.value();
    }

    public String getValue(PathTemplate pathTemplate) {
        return pathTemplate.value();
    }
}
