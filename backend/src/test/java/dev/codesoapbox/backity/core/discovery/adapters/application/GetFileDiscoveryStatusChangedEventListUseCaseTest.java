package dev.codesoapbox.backity.core.discovery.adapters.application;

import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetFileDiscoveryStatusChangedEventListUseCaseTest {

    private GetFileDiscoveryStatusListUseCase useCase;

    @Mock
    private FileDiscoveryService fileDiscoveryService;

    @BeforeEach
    void setUp() {
        useCase = new GetFileDiscoveryStatusListUseCase(fileDiscoveryService);
    }

    @Test
    void shouldGetStatusList() {
        List<FileDiscoveryStatusChangedEvent> statuses =
                singletonList(new FileDiscoveryStatusChangedEvent("someSource", true));
        when(fileDiscoveryService.getStatuses())
                .thenReturn(statuses);

        List<FileDiscoveryStatusChangedEvent> result = useCase.getStatusList();

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(statuses);
    }
}