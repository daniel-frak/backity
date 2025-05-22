package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.application.usecases.EnqueueFileCopyUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers.FileCopyActionsRestResource;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
@ControllerTest
class EnqueueFileCopyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnqueueFileCopyUseCase useCase;

    @Test
    void shouldEnqueueFileCopy() throws Exception {
        var gameFileId = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");
        var backupTargetId = new BackupTargetId("224440e2-6e5c-4f24-94ac-3222587652f7");
        var fileCopyNaturalId = new FileCopyNaturalId(gameFileId, backupTargetId);

        mockMvc.perform(post("/api/" + FileCopyActionsRestResource.RESOURCE_URL + "/enqueue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fileCopyNaturalId": {
                                        "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                        "backupTargetId": "224440e2-6e5c-4f24-94ac-3222587652f7"
                                    }
                                }
                                """))
                .andDo(print())
                .andExpect(status().isOk());

        verify(useCase).enqueue(fileCopyNaturalId);
    }

    @Test
    void shouldReturnBadRequestWhenFileNotFound(CapturedOutput capturedOutput) throws Exception {
        var gameFileUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var backupTargetUuid = "224440e2-6e5c-4f24-94ac-3222587652f7";
        var gameFileId = new GameFileId(gameFileUuid);
        var backupTargetId = new BackupTargetId(backupTargetUuid);
        var fileCopyNaturalId = new FileCopyNaturalId(gameFileId, backupTargetId);
        doThrow(new FileCopyNotFoundException(gameFileId, backupTargetId))
                .when(useCase).enqueue(fileCopyNaturalId);

        mockMvc.perform(post("/api/" + FileCopyActionsRestResource.RESOURCE_URL + "/enqueue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "fileCopyNaturalId": {
                                        "gameFileId": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                        "backupTargetId": "224440e2-6e5c-4f24-94ac-3222587652f7"
                                    }
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());

        assertThat(capturedOutput.getOut())
                .contains("Could not enqueue file copy.")
                .contains(gameFileUuid)
                .contains(backupTargetUuid);
    }
}