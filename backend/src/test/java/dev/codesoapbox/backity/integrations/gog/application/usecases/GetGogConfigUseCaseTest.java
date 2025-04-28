package dev.codesoapbox.backity.integrations.gog.application.usecases;

import dev.codesoapbox.backity.integrations.gog.application.GogConfigInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetGogConfigUseCaseTest {

    private static final GogConfigInfo GOG_CONFIG_INFO = new GogConfigInfo("someUserAuthUrl");

    private GetGogConfigUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetGogConfigUseCase(GOG_CONFIG_INFO);
    }

    @Test
    void shouldGetGogConfig() {
        GogConfigInfo result = useCase.getGogConfig();

        assertThat(result).isEqualTo(GOG_CONFIG_INFO);
    }
}