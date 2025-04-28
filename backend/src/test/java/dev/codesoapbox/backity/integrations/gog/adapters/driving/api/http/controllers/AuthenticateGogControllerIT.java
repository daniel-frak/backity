package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import dev.codesoapbox.backity.integrations.gog.application.usecases.AuthenticateGogUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class AuthenticateGogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticateGogUseCase useCase;

    @Test
    void shouldAuthenticate() throws Exception {
        var code = "1234";
        var refreshToken = "someRefreshToken";
        var expectedJson = """
                {
                    "refresh_token": "%s"
                }
                """.formatted(refreshToken);

        when(useCase.authenticateAndGetRefreshToken(code))
                .thenReturn(refreshToken);

        mockMvc.perform(post("/api/" + GogAuthRestResource.RESOURCE_URL + "?code=" + code))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}