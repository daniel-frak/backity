package dev.codesoapbox.backity.core.gamefiledetails.adapters.driving.api.http.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Game file details", description = "Everything to do with game file details")
public @interface GameFileDetailsTag {
}
