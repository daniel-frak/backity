package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers.FileCopiesRestResource;
import dev.codesoapbox.backity.core.filecopy.application.usecases.DeleteFileCopyUseCase;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class DeleteFileCopyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeleteFileCopyUseCase useCase;

    @Test
    void shouldDeleteFileCopy() throws Exception {
        var stringUuid = "6df888e8-90b9-4df5-a237-0cba422c0310";

        mockMvc.perform(delete("/api/" + FileCopiesRestResource.RESOURCE_URL + "/" + stringUuid))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(useCase).deleteFileCopy(new FileCopyId(stringUuid));
    }
}