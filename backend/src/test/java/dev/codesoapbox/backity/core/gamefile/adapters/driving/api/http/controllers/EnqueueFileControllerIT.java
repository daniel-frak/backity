package dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.application.usecases.EnqueueFileUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotFoundException;
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
class EnqueueFileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnqueueFileUseCase useCase;

    @Test
    void shouldEnqueueFile() throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";

        mockMvc.perform(post("/api/" + GameFileActionsRestResource.RESOURCE_URL + "/enqueue/" + stringUuid))
                .andDo(print())
                .andExpect(status().isOk());

        verify(useCase).enqueue(new GameFileId(stringUuid));
    }

    @Test
    void shouldNotEnqueueWhenFileNotFound(CapturedOutput capturedOutput) throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        var id = new GameFileId(stringUuid);
        doThrow(new GameFileNotFoundException(id))
                .when(useCase).enqueue(id);

        mockMvc.perform(post("/api/" + GameFileActionsRestResource.RESOURCE_URL + "/enqueue/" + stringUuid))
                .andDo(print())
                .andExpect(status().isBadRequest());

        assertThat(capturedOutput.getOut())
                .contains("Could not enqueue file.")
                .contains(stringUuid);
    }
}