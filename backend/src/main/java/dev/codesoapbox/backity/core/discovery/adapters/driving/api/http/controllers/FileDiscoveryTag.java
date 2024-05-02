package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "File discovery", description = "Everything to do with discovering files available for download")
public @interface FileDiscoveryTag {
}
