package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import lombok.*;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Getter
public class BackupTarget {

    @EqualsAndHashCode.Include
    @NonNull
    private final BackupTargetId id;

    @NonNull
    private final StorageSolutionId storageSolutionId;

    @NonNull
    @Setter
    private String name;

    @NonNull
    private String pathTemplate;
}
