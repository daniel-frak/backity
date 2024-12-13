package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import dev.codesoapbox.backity.integrations.gog.application.RefreshGogAccessTokenUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class RefreshGogAccessTokenControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RefreshGogAccessTokenUseCase useCase;

    @Test
    void shouldRefreshAccessToken() throws Exception {
        var refreshToken = "someRefreshToken";
        var accessToken = "someAccessToken";
        var expectedJson = """
                {
                    "refresh_token": "%s"
                }
                """.formatted(accessToken);
        when(useCase.refreshAccessToken(refreshToken))
                .thenReturn(accessToken);

        mockMvc.perform(put("/api/" + GogAuthRestResource.RESOURCE_URL + "?refresh_token=" + refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}