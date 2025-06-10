package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryOverview;
import dev.codesoapbox.backity.core.discovery.application.usecases.GetGameContentDiscoveryOverviewsUseCase;
import dev.codesoapbox.backity.core.discovery.domain.TestGameContentDiscoveryResult;
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
class GetGameContentDiscoveryOverviewsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetGameContentDiscoveryOverviewsUseCase useCase;

    @Test
    void shouldGetDiscoveryOverviews() throws Exception {
        var overview = new GameContentDiscoveryOverview(
                new GameProviderId("TestGameProviderId"),
                true,
                TestGameContentDiscoveryProgress.twentyFivePercentGog(),
                TestGameContentDiscoveryResult.gog()
        );
        when(useCase.getDiscoveryOverviews())
                .thenReturn(List.of(overview));
        var expectedJson = """
                [{
                  "gameProviderId": "TestGameProviderId",
                  "isInProgress": true,
                  "progress": {
                    "percentage": 25,
                    "timeLeftSeconds": 10
                  },
                  "lastDiscoveryResult": {
                    "startedAt": "2022-04-29T15:00:00",
                    "stoppedAt": "2022-04-29T16:00:00",
                    "lastSuccessfulDiscoveryCompletedAt": "2022-04-20T10:00:00",
                    "gamesDiscovered": 5,
                    "gameFilesDiscovered": 70
                  }
                }]
                """;

        mockMvc.perform(get("/api/" + GameContentDiscoveryOverviewsRestResource.RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}