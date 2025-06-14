package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.filecopy.application.usecases.DownloadFileCopyUseCase;
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
class DownloadFileCopyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DownloadFileCopyUseCase useCase;

    @Test
    void shouldDownloadFile() throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";
        var fileCopyId = new FileCopyId(stringUuid);
        byte[] fileContent = "Test file content".getBytes();
        @SuppressWarnings("resource")
        FileResource fileResource = mockFileResourceExists(fileContent, fileCopyId);

        mockMvc.perform(get("/api/" + FileCopiesRestResource.RESOURCE_URL + "/" + stringUuid)
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

    private FileResource mockFileResourceExists(byte[] fileContent, FileCopyId fileCopyId)
            throws FileNotFoundException {
        InputStream inputStream = new ByteArrayInputStream(fileContent);
        FileResource fileResource = new FileResource(inputStream, fileContent.length, "test_file.exe");

        when(useCase.downloadFileCopy(fileCopyId))
                .thenReturn(fileResource);

        return fileResource;
    }

    @Test
    void shouldReturnNotFoundForNonExistentFile() throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";
        var fileCopyId = new FileCopyId(stringUuid);

        when(useCase.downloadFileCopy(fileCopyId))
                .thenThrow(new FileNotFoundException("File not found"));

        mockMvc.perform(get("/api/" + FileCopiesRestResource.RESOURCE_URL + "/" + stringUuid))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorOnUseCaseException() throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";
        var fileCopyId = new FileCopyId(stringUuid);

        when(useCase.downloadFileCopy(fileCopyId))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/api/" + FileCopiesRestResource.RESOURCE_URL + "/" + stringUuid))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldCloseResourceAndRethrowGivenRuntimeExceptionAfterAcquiringResource() throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";
        var fileCopyId = new FileCopyId(stringUuid);
        FileResource fileResource = mockThrowingFileResourceExists(fileCopyId);

        mockMvc.perform(get("/api/" + FileCopiesRestResource.RESOURCE_URL + "/" + stringUuid))
                .andExpect(status().isInternalServerError());
        verify(fileResource).close();
    }

    private FileResource mockThrowingFileResourceExists(FileCopyId fileCopyId) throws FileNotFoundException {
        FileResource fileResource = mock(FileResource.class);
        when(useCase.downloadFileCopy(fileCopyId))
                .thenReturn(fileResource);
        when(fileResource.inputStream())
                .thenThrow(new RuntimeException("Test exception"));

        return fileResource;
    }

    @Test
    void shouldRethrowGivenRuntimeExceptionDuringAcquiringResource() throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";
        var fileCopyId = new FileCopyId(stringUuid);
        when(useCase.downloadFileCopy(fileCopyId))
                .thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(get("/api/" + FileCopiesRestResource.RESOURCE_URL + "/" + stringUuid))
                .andExpect(status().isInternalServerError());
    }
}