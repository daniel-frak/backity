package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Game content discovery", description = "Everything to do with discovering game content")
public @interface GameContentDiscoveryTag {
}
