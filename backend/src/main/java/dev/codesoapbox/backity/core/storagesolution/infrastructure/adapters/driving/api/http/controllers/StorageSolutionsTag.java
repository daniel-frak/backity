package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Storage solutions", description = "Everything to do with storage solutions")
public @interface StorageSolutionsTag {
}
