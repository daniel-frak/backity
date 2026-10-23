package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@FileCopiesTag
@RestController
@RequestMapping(FileCopyQueueRestResource.RESOURCE_URL)
@Validated
public @interface FileCopyQueueRestResource {

    String RESOURCE_URL = "/file-copy-queue";
}
