package dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filemanagement.domain.FileResource;
import dev.codesoapbox.backity.core.gamefile.application.usecases.DownloadFileUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
class DownloadFileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DownloadFileUseCase useCase;

    @Test
    void shouldDownloadFile() throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var gameFileId = new GameFileId(stringUuid);
        byte[] fileContent = "Test file content".getBytes();
        @SuppressWarnings("resource")
        FileResource fileResource = mockFileResourceExists(fileContent, gameFileId);

        mockMvc.perform(get("/api/"
                            + FileBackupsRestResource.RESOURCE_URL.replace("{gameFileId}", stringUuid))
                        .accept(MediaType.APPLICATION_OCTET_STREAM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(fileContent))
                .andExpect(header().longValue("Content-Length", fileContent.length))
                .andExpect(header().stringValues("Access-Control-Expose-Headers", "Content-Disposition"))
                .andExpect(header().stringValues("Content-Disposition",
                        "attachment; filename=\"" + fileResource.fileName() + "\""));
    }

    private FileResource mockFileResourceExists(byte[] fileContent, GameFileId gameFileId)
            throws FileNotFoundException {
        InputStream inputStream = new ByteArrayInputStream(fileContent);
        FileResource fileResource = new FileResource(inputStream, fileContent.length, "test_file.exe");

        when(useCase.downloadFile(gameFileId))
                .thenReturn(fileResource);

        return fileResource;
    }

    @Test
    void shouldReturnNotFoundForNonExistentFile() throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var gameFileId = new GameFileId(stringUuid);

        when(useCase.downloadFile(gameFileId))
                .thenThrow(new FileNotFoundException("File not found"));

        mockMvc.perform(get("/api/"
                            + FileBackupsRestResource.RESOURCE_URL.replace("{gameFileId}", stringUuid)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorOnUseCaseException() throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var gameFileId = new GameFileId(stringUuid);

        when(useCase.downloadFile(gameFileId))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/"
                            + FileBackupsRestResource.RESOURCE_URL.replace("{gameFileId}", stringUuid)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldCloseResourceAndRethrowGivenRuntimeExceptionAfterAcquiringResource() throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var gameFileId = new GameFileId(stringUuid);
        FileResource fileResource = mockThrowingFileResourceExists(gameFileId);

        mockMvc.perform(get("/api/"
                            + FileBackupsRestResource.RESOURCE_URL.replace("{gameFileId}", stringUuid)))
                .andExpect(status().isInternalServerError());
        verify(fileResource).close();
    }

    private FileResource mockThrowingFileResourceExists(GameFileId gameFileId) throws FileNotFoundException {
        FileResource fileResource = mock(FileResource.class);
        when(useCase.downloadFile(gameFileId))
                .thenReturn(fileResource);
        when(fileResource.inputStream())
                .thenThrow(new RuntimeException("Test exception"));

        return fileResource;
    }

    @Test
    void shouldRethrowGivenRuntimeExceptionDuringAcquiringResource() throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var gameFileId = new GameFileId(stringUuid);
        when(useCase.downloadFile(gameFileId))
                .thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(get("/api/"
                            + FileBackupsRestResource.RESOURCE_URL.replace("{gameFileId}", stringUuid)))
                .andExpect(status().isInternalServerError());
    }
}