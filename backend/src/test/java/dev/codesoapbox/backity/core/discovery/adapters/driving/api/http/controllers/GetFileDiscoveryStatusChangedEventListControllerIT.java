package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.adapters.application.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class GetFileDiscoveryStatusChangedEventListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetFileDiscoveryStatusListUseCase useCase;

    @Test
    void shouldGetStatuses() throws Exception {
        var status = new FileDiscoveryStatusChangedEvent("someSource", true);

        when(useCase.getStatusList())
                .thenReturn(singletonList(status));

        var expectedJson = """
                [{
                  "source": "someSource",
                  "isInProgress": true
                }]""";

        mockMvc.perform(get("/api/" + FileDiscoveryRestResource.RESOURCE_URL + "/statuses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}