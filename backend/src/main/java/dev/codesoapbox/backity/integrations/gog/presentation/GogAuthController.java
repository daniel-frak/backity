package dev.codesoapbox.backity.integrations.gog.presentation;

import dev.codesoapbox.backity.integrations.gog.application.dto.auth.RefreshTokenResponse;
import dev.codesoapbox.backity.integrations.gog.application.services.auth.GogAuthService;
import dev.codesoapbox.backity.shared.presentation.ApiControllerPaths;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "GOG authentication")
@RestController
@RequestMapping(ApiControllerPaths.API + "gog/auth")
@RequiredArgsConstructor
public class GogAuthController {

    private final GogAuthService authService;

    @GetMapping
    public RefreshTokenResponse authenticate(@RequestParam("code") String code) {
        authService.authenticate(code);
        return RefreshTokenResponse.of(authService.getRefreshToken());
    }

    @GetMapping("/check")
    public boolean check() {
        return authService.isAuthenticated();
    }

    @GetMapping("refresh")
    public RefreshTokenResponse refresh(@RequestParam("refresh_token") String refreshToken) {
        authService.refresh(refreshToken);
        return RefreshTokenResponse.of(authService.getRefreshToken());
    }
}
