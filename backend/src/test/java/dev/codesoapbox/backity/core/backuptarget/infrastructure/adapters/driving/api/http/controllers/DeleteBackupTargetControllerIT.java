package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backuptarget.application.DeleteBackupTargetUseCase;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class DeleteBackupTargetControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeleteBackupTargetUseCase useCase;

    @Test
    void shouldReturnHttp400GivenInvalidUuid() throws Exception {
        mockMvc.perform(delete(
                        "/api/" + BackupTargetsRestResource.RESOURCE_URL + "/invalidUuid"))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldDeleteBackupTarget() throws Exception {
        BackupTargetId backupTargetId = aBackupTargetId();

        mockMvc.perform(delete(
                        "/api/" + BackupTargetsRestResource.RESOURCE_URL + "/" + backupTargetId.value()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(useCase).deleteBackupTarget(backupTargetId);
    }

    private BackupTargetId aBackupTargetId() {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        return backupTarget.getId();
    }
}