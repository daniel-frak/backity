package dev.codesoapbox.gogbackupservice.presentation.controllers;

import dev.codesoapbox.gogbackupservice.shared.presentation.controllers.ApiControllerPaths;
import dev.codesoapbox.gogbackupservice.presentation.viewmodel.HomePageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiControllerPaths.API + "/home", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Home page")
public class HomePageController {

    @GetMapping
    @Operation(summary = "Returns unsecured test data from backend")
    public ResponseEntity<HomePageDto> getHomePageData() {
        var body = new HomePageDto("working");

        return ResponseEntity.ok(body);
    }
}
