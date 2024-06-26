package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import dev.codesoapbox.backity.integrations.gog.application.CheckGogAuthenticationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class CheckGogAuthenticationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CheckGogAuthenticationUseCase useCase;

    @Test
    void shouldCheckAuthentication() throws Exception {
        when(useCase.isAuthenticated())
                .thenReturn(true);

        mockMvc.perform(get("/api/gog/auth/check"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}