package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.usecases.StopFileDiscoveryUseCase;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class StopFileDiscoveryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StopFileDiscoveryUseCase useCase;

    @Test
    void shouldStopFileDiscovery() throws Exception {
        mockMvc.perform(post("/api/" + FileDiscoveryActionsRestResource.RESOURCE_URL + "/stop"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(useCase).stopFileDiscovery();
    }
}