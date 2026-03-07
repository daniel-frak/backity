package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backuptarget.application.AddBackupTargetCommand;
import dev.codesoapbox.backity.core.backuptarget.application.AddBackupTargetUseCase;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest
class AddBackupTargetControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddBackupTargetUseCase useCase;

    @Autowired
    private JsonMapper jsonMapper;

    private static Stream<Arguments> emptyFieldCases() {
        return Stream.of(
                Arguments.of("name", "\t"),
                Arguments.of("storageSolutionId", "\t"),
                Arguments.of("pathTemplate", "\t")
        );
    }

    @ParameterizedTest(name = "should return Http 400 given empty {0}", quoteTextArguments = false)
    @MethodSource("emptyFieldCases")
    void shouldReturnHttp400GivenEmptyField(String field, String value) throws Exception {
        Map<String, Object> requestBody = validRequestBodyMap();
        requestBody.put(field, value);

        mockMvc.perform(post("/api/" + BackupTargetsRestResource.RESOURCE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private Map<String, Object> validRequestBodyMap() {
        return new HashMap<>(Map.of(
                "name", "Local folder",
                "storageSolutionId", "storageSolution1",
                "pathTemplate", "games/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}"
        ));
    }

    @Test
    void shouldAddBackupTarget() throws Exception {
        BackupTarget expectedBackupTarget = TestBackupTarget.localFolderBuilder()
                .withId(new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76"))
                .withName("Local folder")
                .withStorageSolutionId(new StorageSolutionId("storageSolution1"))
                .withPathTemplate("games/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}")
                .build();
        AddBackupTargetCommand command = toAddBackupTargetCommand(expectedBackupTarget);
        when(useCase.addBackupTarget(command))
                .thenReturn(expectedBackupTarget);

        mockMvc.perform(post("/api/" + BackupTargetsRestResource.RESOURCE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Local folder",
                                    "storageSolutionId": "storageSolution1",
                                    "pathTemplate": "games/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                            "backupTarget": {
                                "id": "eda52c13-ddf7-406f-97d9-d3ce2cab5a76",
                                "name": "Local folder",
                                "storageSolutionId": "storageSolution1",
                                "pathTemplate": "games/{GAME_PROVIDER_ID}/{TITLE}/{FILENAME}"
                            }
                        }
                        """));
    }

    private AddBackupTargetCommand toAddBackupTargetCommand(BackupTarget expectedBackupTarget) {
        return new AddBackupTargetCommand(
                expectedBackupTarget.getName(),
                expectedBackupTarget.getStorageSolutionId(),
                expectedBackupTarget.getPathTemplate()
        );
    }
}