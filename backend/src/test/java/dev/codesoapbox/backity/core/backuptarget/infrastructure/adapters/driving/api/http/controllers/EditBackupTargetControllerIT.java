package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backuptarget.application.EditBackupTargetCommand;
import dev.codesoapbox.backity.core.backuptarget.application.EditBackupTargetUseCase;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.testing.http.annotations.ControllerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class EditBackupTargetControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EditBackupTargetUseCase useCase;

    @Autowired
    private JsonMapper jsonMapper;

    private static Stream<Arguments> emptyFieldCases() {
        return Stream.of(
                Arguments.of("name", "\t")
        );
    }

    @ParameterizedTest(name = "should return Http 400 given empty {0}", quoteTextArguments = false)
    @MethodSource("emptyFieldCases")
    void shouldReturnHttp400GivenEmptyField(String field, String value) throws Exception {
        BackupTargetId backupTargetId = aBackupTargetId();
        Map<String, Object> requestBody = validRequestBodyMap();
        requestBody.put(field, value);

        mockMvc.perform(put("/api/" + BackupTargetsRestResource.RESOURCE_URL + "/" + backupTargetId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private Map<String, Object> validRequestBodyMap() {
        return new HashMap<>(Map.of(
                "name", "Local folder"
        ));
    }

    private BackupTargetId aBackupTargetId() {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        return backupTarget.getId();
    }

    @Test
    void shouldEditBackupTarget() throws Exception {
        BackupTargetId backupTargetId = aBackupTargetId();
        var command = new EditBackupTargetCommand(
                backupTargetId,
                "Local folder"
        );

        mockMvc.perform(put("/api/" + BackupTargetsRestResource.RESOURCE_URL + "/" + backupTargetId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Local folder"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(useCase).editBackupTarget(command);
    }
}