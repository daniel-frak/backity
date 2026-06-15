package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringSchedulerSliceConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringSchedulerSliceConfiguration
public class SchedulingConfig {
}
