package dev.codesoapbox.backity.home.presentation.controllers;

import dev.codesoapbox.backity.home.application.dto.HomePageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/home", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Home page")
public class HomePageController {

    @GetMapping
    @Operation(summary = "Returns unsecured test data from backend")
    public ResponseEntity<HomePageResponse> getHomePageData() {
        var body = new HomePageResponse("working");

        return ResponseEntity.ok(body);
    }
}
