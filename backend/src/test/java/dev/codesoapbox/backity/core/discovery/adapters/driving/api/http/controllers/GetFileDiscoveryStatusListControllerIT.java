package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.application.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryStatus;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class GetFileDiscoveryStatusListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetFileDiscoveryStatusListUseCase useCase;

    @Test
    void shouldGetStatuses() throws Exception {
        var status = new FileDiscoveryStatus("someGameProviderId", true);

        when(useCase.getStatusList())
                .thenReturn(List.of(status));

        var expectedJson = """
                [{
                  "gameProviderId": "someGameProviderId",
                  "isInProgress": true
                }]""";

        mockMvc.perform(get("/api/" + FileDiscoveryStatusRestResource.RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}