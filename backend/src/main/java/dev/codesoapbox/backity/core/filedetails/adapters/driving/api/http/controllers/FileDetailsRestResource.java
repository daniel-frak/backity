package dev.codesoapbox.backity.core.filedetails.adapters.driving.api.http.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static dev.codesoapbox.backity.core.filedetails.adapters.driving.api.http.controllers.FileDetailsRestResource.RESOURCE_URL;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@FileDetailsTag
@RestController
@RequestMapping(RESOURCE_URL)
@Validated
public @interface FileDetailsRestResource {

    String RESOURCE_URL = "file-details";
}
