package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;

public class TestFileCopyEnqueuedEvent {

    public static FileCopyEnqueuedEvent any() {
        return new FileCopyEnqueuedEvent(new FileCopyId("6b2260b6-87b8-41a4-9c22-8e56b7e68e54"));
    }
}