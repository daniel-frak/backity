package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.application.usecases.CancelFileCopyUseCase;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotFoundException;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class CancelFileCopyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CancelFileCopyUseCase useCase;

    @Test
    void shouldCancelFileCopy() throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";

        mockMvc.perform(delete("/api/" + FileCopyQueueRestResource.RESOURCE_URL + "/" + stringUuid))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(useCase).execute(new FileCopyId(stringUuid));
    }

    @Test
    void shouldReturnNotFoundForNonExistentFileCopy() throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";
        noFileCopiesExist();

        mockMvc.perform(delete("/api/" + FileCopyQueueRestResource.RESOURCE_URL + "/" + stringUuid))
                .andExpect(status().isNotFound());
    }

    private void noFileCopiesExist() {
        doAnswer(inv -> {
            throw new FileCopyNotFoundException(inv.getArgument(0));
        }).when(useCase).execute(any());
    }
}