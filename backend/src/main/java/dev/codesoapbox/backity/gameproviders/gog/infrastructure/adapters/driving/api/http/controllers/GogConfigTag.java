package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "GOG configuration", description = "Everything to do with managing the GOG API configuration")
public @interface GogConfigTag {
}
