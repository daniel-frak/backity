package dev.codesoapbox.backity.core.files.downloading.domain.model.messages;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@IncludeInDocumentation
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadProgress {

    private int percentage;
    private long timeLeftSeconds;
}
