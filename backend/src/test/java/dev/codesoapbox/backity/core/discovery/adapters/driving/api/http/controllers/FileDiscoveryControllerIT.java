package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.discovery.config.FileDiscoveryControllerBeanConfig;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.shared.config.jpa.SharedControllerBeanConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileDiscoveryController.class)
@Import({SharedControllerBeanConfig.class, FileDiscoveryControllerBeanConfig.class})
class FileDiscoveryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileDiscoveryService fileDiscoveryService;

    @Test
    void shouldStartFileDiscovery() throws Exception {
        mockMvc.perform(get("/api/file-discovery/discover"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(fileDiscoveryService).startFileDiscovery();
    }

    @Test
    void shouldStopFileDiscovery() throws Exception {
        mockMvc.perform(get("/api/file-discovery/stop-discovery"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(fileDiscoveryService).stopFileDiscovery();
    }

    @Test
    void shouldGetStatuses() throws Exception {
        var status = new FileDiscoveryStatus("someSource", true);

        when(fileDiscoveryService.getStatuses())
                .thenReturn(singletonList(status));

        var expectedJson = """
                [{
                  "source": "someSource",
                  "inProgress": true
                }]""";

        mockMvc.perform(get("/api/file-discovery/statuses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}