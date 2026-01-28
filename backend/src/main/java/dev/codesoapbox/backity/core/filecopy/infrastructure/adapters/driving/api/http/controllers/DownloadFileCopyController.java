package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.application.usecases.DownloadFileCopyUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
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

@FileCopiesRestResource
@RequiredArgsConstructor
public class DownloadFileCopyController {

    private final DownloadFileCopyUseCase useCase;

    @SuppressWarnings("java:S1166") // No need to log or rethrow exception when file not found
    @Operation(summary = "Download file copy", description = "Returns a file copy as a resource")
    @GetMapping("{id}")
    public ResponseEntity<InputStreamResource> downloadFileCopy(
            @SuppressWarnings("java:S6856")
            @PathVariable("id") String idValue) throws IOException {
        var fileCopyId = new FileCopyId(idValue);

        FileResource fileResource = null;
        try {
            fileResource = useCase.downloadFileCopy(fileCopyId);
            var inputStreamResource = new InputStreamResource(fileResource.inputStream());
            return ResponseEntity.ok()
                    .contentLength(fileResource.sizeInBytes())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                                                             + fileResource.fileName() + "\"")
                    .body(inputStreamResource);
        } catch (FileNotFoundException _) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            if (fileResource != null) {
                fileResource.close();
            }
            throw e;
        }
    }
}

