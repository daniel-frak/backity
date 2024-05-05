package dev.codesoapbox.backity.core.filedetails.domain.exceptions;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;

public class FileDetailsNotFoundException extends RuntimeException {

    public FileDetailsNotFoundException(FileDetailsId id) {
        super("Could not find " + FileDetails.class.getSimpleName() + " with id=" + id);
    }
}
