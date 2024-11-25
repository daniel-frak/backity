package dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers.FileBackupsRestResource.RESOURCE_URL;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@FileBackupTag
@RestController
@RequestMapping(RESOURCE_URL)
@Validated
public @interface FileBackupsRestResource {

    String RESOURCE_URL = GameFileRestResource.RESOURCE_URL + "/{gameFileId}/file-backup";
}
