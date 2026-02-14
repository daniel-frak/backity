package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Getter
@ToString
public class BackupTarget {

    @EqualsAndHashCode.Include
    @NonNull
    private final BackupTargetId id;

    private final LocalDateTime dateCreated; // Provided by DB
    private final LocalDateTime dateModified; // Provided by DB

    @NonNull
    private final StorageSolutionId storageSolutionId;

    @NonNull
    @Setter
    private String name;

    @NonNull
    private PathTemplate pathTemplate;

    public static BackupTarget create(String name, StorageSolutionId storageSolutionId, PathTemplate pathTemplate) {
        return new BackupTarget(
                BackupTargetId.newInstance(),
                null,
                null,
                storageSolutionId,
                name,
                pathTemplate
        );
    }
}
