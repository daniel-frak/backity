package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model.RefreshTokenJsonResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "GOG authentication", description = "Everything to do with managing a GOG login session")
@RestController
@RequestMapping("gog/auth")
@RequiredArgsConstructor
public class GogAuthController {

    private final GogAuthService authService;

    @Operation(summary = "Authenticate", description = "Returns a refresh token based on a code")
    @GetMapping
    public RefreshTokenJsonResponse authenticate(@RequestParam("code") String code) {
        authService.authenticate(code);
        return RefreshTokenJsonResponse.of(authService.getRefreshToken());
    }

    @Operation(summary = "Check authentication",
            description = "Returns whether or not the user is currently authenticated")
    @GetMapping("/check")
    public boolean checkAuthentication() {
        return authService.isAuthenticated();
    }

    @Operation(summary = "Refresh access token", description = "Refreshes the access token using a refresh token")
    @GetMapping("refresh")
    public RefreshTokenJsonResponse refreshAccessToken(@RequestParam("refresh_token") String refreshToken) {
        authService.refresh(refreshToken);
        return RefreshTokenJsonResponse.of(authService.getRefreshToken());
    }
}
