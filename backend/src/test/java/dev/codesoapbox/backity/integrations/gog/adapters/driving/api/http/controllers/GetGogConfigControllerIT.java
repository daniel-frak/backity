package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.application.GetGogConfigUseCase;
import dev.codesoapbox.backity.integrations.gog.application.GogConfigInfo;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class GetGogConfigControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetGogConfigUseCase useCase;

    @Test
    void shouldGetGogConfig() throws Exception {
        when(useCase.getGogConfig())
                .thenReturn(new GogConfigInfo("someUserAuthUrl"));

        String expectedResponseJson = """
                {
                  "userAuthUrl": "someUserAuthUrl"
                }
                """;
        mockMvc.perform(get("/api/gog/config"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseJson));
    }
}