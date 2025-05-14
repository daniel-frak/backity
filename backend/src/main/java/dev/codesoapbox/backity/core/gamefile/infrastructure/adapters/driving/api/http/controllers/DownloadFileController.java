package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.gamefile.application.usecases.DownloadFileUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.FileNotFoundException;
import java.io.IOException;

@FileBackupsRestResource
@RequiredArgsConstructor
public class DownloadFileController {

    private final DownloadFileUseCase useCase;

    @SuppressWarnings("java:S1166") // No need to log or rethrow exception when file not found
    @Operation(summary = "Download file", description = "Returns the file currently being downloaded")
    @GetMapping
    public ResponseEntity<InputStreamResource> downloadFile(
            @SuppressWarnings("java:S6856")
            @PathVariable(FileBackupsRestResource.GAME_FILE_ID) String gameFileIdValue) throws IOException {
        var gameFileId = new GameFileId(gameFileIdValue);

        FileResource fileResource = null;
        try {
            fileResource = useCase.downloadFile(gameFileId);
            var inputStreamResource = new InputStreamResource(fileResource.inputStream());
            return ResponseEntity.ok()
                    .contentLength(fileResource.sizeInBytes())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                                                             + fileResource.fileName() + "\"")
                    .body(inputStreamResource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            if (fileResource != null) {
                fileResource.close();
            }
            throw e;
        }
    }
}

