package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backup.application.FileCopyFactory;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnqueueFileCopyUseCaseTest {

    private EnqueueFileCopyUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private FileCopyFactory fileCopyFactory;

    @BeforeEach
    void setUp() {
        useCase = new EnqueueFileCopyUseCase(fileCopyRepository, fileCopyFactory);
    }

    @Test
    void shouldSetFileCopyStatusToEnqueuedAndPersistIt() {
        FileCopy fileCopy = mockDiscoveredFileCopyExists();

        useCase.enqueue(fileCopy.getNaturalId());

        assertThat(fileCopy.getStatus()).isEqualTo(FileCopyStatus.ENQUEUED);
        verify(fileCopyRepository).save(fileCopy);
    }

    private FileCopy mockDiscoveredFileCopyExists() {
        FileCopy fileCopy = TestFileCopy.discovered();
        when(fileCopyRepository.findByNaturalIdOrCreate(eq(fileCopy.getNaturalId()), any()))
                .thenAnswer(inv -> {
                    checkFactoryWasPassed(inv);
                    return fileCopy;
                });

        return fileCopy;
    }

    private void checkFactoryWasPassed(InvocationOnMock inv) {
        inv.getArgument(1, Supplier.class).get();
    }

    @Test
    void shouldPassFileCopySupplierToRepository() {
        FileCopy fileCopy = mockDiscoveredFileCopyExists();
        AtomicBoolean factoryWasPassed = trackFactoryWasPassed(fileCopy);

        useCase.enqueue(fileCopy.getNaturalId());

        assertThat(factoryWasPassed).isTrue();
    }

    private AtomicBoolean trackFactoryWasPassed(FileCopy fileCopy) {
        AtomicBoolean factoryWasPassed = new AtomicBoolean(false);
        when(fileCopyFactory.create(any()))
                .thenAnswer(inv -> {
                    if(inv.getArgument(0).equals(fileCopy.getNaturalId())) {
                        factoryWasPassed.set(true);
                    }
                    return null;
                });
        return factoryWasPassed;
    }
}