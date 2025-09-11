package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.gameproviders.gog.application.usecases.AuthenticateGogUseCase;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

        mockMvc.perform(post("/api/" + GogAuthRestResource.RESOURCE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "code": "%s"
                                }
                                """.formatted(code))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}