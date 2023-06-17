package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GogAuthController.class)
class GogAuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GogAuthService authService;

    @Test
    void shouldAuthenticate() throws Exception {
        var code = "1234";
        var refreshToken = "someRefreshToken";
        var expectedJson = """
                {
                    "refresh_token": "%s"
                }
                """.formatted(refreshToken);

        when(authService.getRefreshToken())
                .thenReturn(refreshToken);

        mockMvc.perform(get("/api/gog/auth?code=" + code))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(authService).authenticate(code);
    }

    @Test
    void shouldCheckAuthentication() throws Exception {
        when(authService.isAuthenticated())
                .thenReturn(true);

        mockMvc.perform(get("/api/gog/auth/check"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldRefreshAccessToken() throws Exception {
        var refreshToken = "someRefreshToken";
        var expectedJson = """
                {
                    "refresh_token": "%s"
                }
                """.formatted(refreshToken);

        when(authService.getRefreshToken())
                .thenReturn(refreshToken);

        mockMvc.perform(get("/api/gog/auth/refresh?refresh_token=" + refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));

        verify(authService).refresh(refreshToken);
    }
}