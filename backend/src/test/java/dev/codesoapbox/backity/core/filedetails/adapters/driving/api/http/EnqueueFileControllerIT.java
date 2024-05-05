package dev.codesoapbox.backity.core.filedetails.adapters.driving.api.http;

import dev.codesoapbox.backity.core.filedetails.adapters.driving.api.http.controllers.FileDetailsRestResource;
import dev.codesoapbox.backity.core.filedetails.application.EnqueueFileUseCase;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import dev.codesoapbox.backity.core.filedetails.domain.exceptions.FileDetailsNotFoundException;
import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        mockMvc.perform(get("/api/" + FileDetailsRestResource.RESOURCE_URL + "/enqueue/" + stringUuid))
                .andDo(print())
                .andExpect(status().isOk());

        verify(useCase).enqueue(new FileDetailsId(UUID.fromString(stringUuid)));
    }

    @Test
    void shouldNotDownloadWhenFileNotFound(CapturedOutput capturedOutput) throws Exception {
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";
        FileDetailsId id = new FileDetailsId(UUID.fromString(stringUuid));
        doThrow(new FileDetailsNotFoundException(id))
                .when(useCase).enqueue(id);

        mockMvc.perform(get("/api/" + FileDetailsRestResource.RESOURCE_URL + "/enqueue/" + stringUuid))
                .andDo(print())
                .andExpect(status().isBadRequest());

        assertThat(capturedOutput.getOut())
                .contains("Could not enqueue file.")
                .contains(stringUuid);
    }
}