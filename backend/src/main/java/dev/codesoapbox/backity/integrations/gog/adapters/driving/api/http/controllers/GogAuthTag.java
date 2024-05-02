package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "GOG authentication", description = "Everything to do with managing a GOG login session")
public @interface GogAuthTag {
}
