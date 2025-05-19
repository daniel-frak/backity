package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.FileSourceUrlEmptyException;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;

public record FileSource(
        @NonNull GameProviderId gameProviderId,
        @NonNull String originalGameTitle,
        @NonNull String fileTitle,
        @NonNull String version,
        @NonNull String url,
        @NonNull String originalFileName,
        @NonNull FileSize size
) {

    public FileSource {
        if (Strings.isBlank(url)) {
            throw new FileSourceUrlEmptyException(gameProviderId);
        }
    }
}
