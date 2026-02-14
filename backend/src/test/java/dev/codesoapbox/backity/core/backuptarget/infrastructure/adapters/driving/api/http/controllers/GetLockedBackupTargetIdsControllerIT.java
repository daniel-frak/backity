package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backuptarget.application.GetLockedBackupTargetIdsUseCase;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
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
class GetLockedBackupTargetIdsControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetLockedBackupTargetIdsUseCase useCase;

    @Test
    void shouldGetLockedBackupTargetIds() throws Exception {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        when(useCase.getLockedBackupTargetIds())
                .thenReturn(List.of(backupTarget.getId()));

        var expectedJson = """
                [
                  "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                ]
                """;
        mockMvc.perform(get("/api/" + LockedBackupTargetsRestResource.RESOURCE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}