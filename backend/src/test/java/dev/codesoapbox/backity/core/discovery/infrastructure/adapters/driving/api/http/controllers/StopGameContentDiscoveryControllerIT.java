package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.usecases.StopGameContentDiscoveryUseCase;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class StopGameContentDiscoveryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StopGameContentDiscoveryUseCase useCase;

    @Test
    void shouldStopFileDiscovery() throws Exception {
        mockMvc.perform(post("/api/" + GameContentDiscoveryActionsRestResource.RESOURCE_URL + "/stop"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(useCase).stopContentDiscovery();
    }
}