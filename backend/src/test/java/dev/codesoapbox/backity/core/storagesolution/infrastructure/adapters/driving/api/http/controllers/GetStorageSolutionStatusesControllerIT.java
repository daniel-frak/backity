package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.storagesolution.application.GetStorageSolutionStatusesUseCase;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class GetStorageSolutionStatusesControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetStorageSolutionStatusesUseCase useCase;

    @Test
    void shouldGetStorageSolutionStatuses() throws Exception {
        when(useCase.getStorageSolutionStatuses())
                .thenReturn(Map.of(
                        new StorageSolutionId("LOCAL_FILE_SYSTEM"), StorageSolutionStatus.CONNECTED,
                        new StorageSolutionId("S3"), StorageSolutionStatus.NOT_CONNECTED
                ));
        var expectedJson = """
                {
                  "statuses": {
                    "LOCAL_FILE_SYSTEM": "CONNECTED",
                    "S3": "NOT_CONNECTED"
                  }
                }
                """;

        mockMvc.perform(get("/api/" + StorageSolutionStatusesRestResource.RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}