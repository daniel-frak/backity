package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.application.usecases.LogOutOfGogUseCase;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class LogOutOfGogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LogOutOfGogUseCase useCase;

    @Test
    void shouldLogOutOfGog() throws Exception {
        mockMvc.perform(delete("/api/" + GogAuthRestResource.RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk());
        verify(useCase).logOutOfGog();
    }
}