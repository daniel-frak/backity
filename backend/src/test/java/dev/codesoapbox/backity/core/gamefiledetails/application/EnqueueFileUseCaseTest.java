package dev.codesoapbox.backity.core.gamefiledetails.application;

import dev.codesoapbox.backity.core.gamefiledetails.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnqueueFileUseCaseTest {

    private EnqueueFileUseCase useCase;

    @Mock
    private GameFileDetailsRepository gameFileDetailsRepository;

    @BeforeEach
    void setUp() {
        useCase = new EnqueueFileUseCase(gameFileDetailsRepository);
    }

    @Test
    void shouldEnqueue() {
        var id = new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"));
        GameFileDetails gameFileDetails = TestGameFileDetails.discoveredFileDetails().build();
        when(gameFileDetailsRepository.getById(id))
                .thenReturn(gameFileDetails);

        useCase.enqueue(id);

        assertThat(gameFileDetails.getBackupDetails().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
        verify(gameFileDetailsRepository).save(gameFileDetails);
    }
}