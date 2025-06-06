package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryStatus;
import dev.codesoapbox.backity.core.discovery.application.usecases.GetGameContentDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryProgress;
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
class GetGameContentDiscoveryStatusListControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetGameContentDiscoveryStatusListUseCase useCase;

    @Test
    void shouldGetStatuses() throws Exception {
        var status = new GameContentDiscoveryStatus(
                new GameProviderId("TestGameProviderId"),
                true,
                TestGameContentDiscoveryProgress.twentyFivePercentGog()
        );
        when(useCase.getStatusList())
                .thenReturn(List.of(status));
        var expectedJson = """
                [{
                  "gameProviderId": "TestGameProviderId",
                  "isInProgress": true,
                  "progress": {
                    "percentage": 25,
                    "timeLeftSeconds": 10
                  }
                }]
                """;

        mockMvc.perform(get("/api/" + GameContentDiscoveryStatusRestResource.RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}