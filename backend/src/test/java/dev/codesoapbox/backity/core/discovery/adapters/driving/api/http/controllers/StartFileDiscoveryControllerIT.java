package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.adapters.application.StartFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        mockMvc.perform(get("/api/" + FileDiscoveryRestResource.RESOURCE_URL + "/discover"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(useCase).startFileDiscovery();
    }
}