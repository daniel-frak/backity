package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@GameContentDiscoveryTag
@RestController
@RequestMapping(GameContentDiscoveryActionsRestResource.RESOURCE_URL)
@Validated
public @interface GameContentDiscoveryActionsRestResource {

    String RESOURCE_URL = "content-discovery-actions";
}
