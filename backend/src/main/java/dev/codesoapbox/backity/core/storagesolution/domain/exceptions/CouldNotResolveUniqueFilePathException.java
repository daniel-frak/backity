package dev.codesoapbox.backity.core.storagesolution.domain.exceptions;

public class CouldNotResolveUniqueFilePathException extends RuntimeException {

    public CouldNotResolveUniqueFilePathException(
            String gameTitle, String fileName, int attemptNumber) {
        super("Could not resolve unique file path for game '" + gameTitle + "' and file '" + fileName
              + "' after " + attemptNumber + " attempts");
    }
}
