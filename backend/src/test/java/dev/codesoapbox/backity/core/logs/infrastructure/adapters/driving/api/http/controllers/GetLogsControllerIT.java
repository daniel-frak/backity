package dev.codesoapbox.backity.core.logs.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.logs.application.GetLogsUseCase;
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
class GetLogsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetLogsUseCase useCase;

    @Test
    void shouldGetLogs() throws Exception {
        var expectedLogs = List.of("someLog");
        var expectedJson = """
                            [
                                "someLog"
                            ]
                """;

        when(useCase.getLogs())
                .thenReturn(expectedLogs);

        mockMvc.perform(get("/api/logs"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}