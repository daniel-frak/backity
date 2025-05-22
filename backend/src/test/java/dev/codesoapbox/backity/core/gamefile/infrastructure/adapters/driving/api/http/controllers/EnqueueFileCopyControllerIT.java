package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers.FileCopyActionsRestResource;
import dev.codesoapbox.backity.core.filecopy.application.usecases.EnqueueFileCopyUseCase;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
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
    void shouldEnqueueFile() throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";

        mockMvc.perform(post("/api/" + FileCopyActionsRestResource.RESOURCE_URL + "/enqueue/" + stringUuid))
                .andDo(print())
                .andExpect(status().isOk());

        verify(useCase).enqueue(new FileCopyId(stringUuid));
    }

    @Test
    void shouldNotEnqueueWhenFileNotFound(CapturedOutput capturedOutput) throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";
        var id = new FileCopyId(stringUuid);
        doThrow(new FileCopyNotFoundException(id))
                .when(useCase).enqueue(id);

        mockMvc.perform(post("/api/" + FileCopyActionsRestResource.RESOURCE_URL + "/enqueue/" + stringUuid))
                .andDo(print())
                .andExpect(status().isBadRequest());

        assertThat(capturedOutput.getOut())
                .contains("Could not enqueue file copy.")
                .contains(stringUuid);
    }
}