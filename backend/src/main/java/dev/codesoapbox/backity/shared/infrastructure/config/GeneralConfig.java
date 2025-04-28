package dev.codesoapbox.backity.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;

@Order(0)
@Configuration
@EnableScheduling
public class GeneralConfig {
}
