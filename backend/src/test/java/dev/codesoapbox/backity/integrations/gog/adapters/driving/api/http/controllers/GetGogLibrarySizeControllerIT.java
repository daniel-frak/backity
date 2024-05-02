package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.shared.config.http.ControllerTest;
import dev.codesoapbox.backity.integrations.gog.application.GetGogLibrarySizeUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class GetGogLibrarySizeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetGogLibrarySizeUseCase useCase;

    @Test
    void shouldGetLibrarySize() throws Exception {
        String expectedSize = "1GB";
        when(useCase.getLibrarySize())
                .thenReturn(expectedSize);

        mockMvc.perform(get("/api/gog/library/size"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedSize));
    }
}