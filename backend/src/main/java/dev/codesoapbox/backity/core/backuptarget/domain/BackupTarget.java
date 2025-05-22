package dev.codesoapbox.backity.core.backuptarget.domain;

import lombok.*;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Getter
public class BackupTarget {

    @EqualsAndHashCode.Include
    @NonNull
    private BackupTargetId id;

    @NonNull
    @Setter
    private String title;

    @NonNull
    private String storageSolutionId;
}
