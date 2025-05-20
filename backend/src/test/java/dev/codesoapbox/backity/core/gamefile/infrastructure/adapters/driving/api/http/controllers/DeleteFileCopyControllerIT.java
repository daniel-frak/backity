package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.gamefile.application.usecases.DeleteFileCopyUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
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
        var stringUuid = "acde26d7-33c7-42ee-be16-bca91a604b48";

        mockMvc.perform(delete("/api/"
                               + FileCopiesRestResource.RESOURCE_URL.replace("{gameFileId}", stringUuid)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(useCase).deleteFileCopy(new GameFileId(stringUuid));
    }
}