package dev.codesoapbox.backity.core.filedetails.application;

import dev.codesoapbox.backity.core.filedetails.domain.*;
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
    private FileDetailsRepository fileDetailsRepository;

    @BeforeEach
    void setUp() {
        useCase = new EnqueueFileUseCase(fileDetailsRepository);
    }

    @Test
    void shouldEnqueue() {
        var id = new FileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"));
        FileDetails fileDetails = TestFileDetails.discoveredFileDetails().build();
        when(fileDetailsRepository.getById(id))
                .thenReturn(fileDetails);

        useCase.enqueue(id);

        assertThat(fileDetails.getBackupDetails().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
        verify(fileDetailsRepository).save(fileDetails);
    }
}