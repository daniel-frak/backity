package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.StartFileDiscoveryUseCase;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class StartFileDiscoveryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StartFileDiscoveryUseCase useCase;

    @Test
    void shouldStartFileDiscovery() throws Exception {
        mockMvc.perform(post("/api/" + FileDiscoveryActionsRestResource.RESOURCE_URL + "/start"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(useCase).startFileDiscovery();
    }
}