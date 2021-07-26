package dev.codesoapbox.gogbackupservice.gog.presentation;

import dev.codesoapbox.gogbackupservice.gog.application.dto.auth.RefreshTokenResponse;
import dev.codesoapbox.gogbackupservice.gog.application.services.auth.GogAuthService;
import dev.codesoapbox.gogbackupservice.shared.presentation.ApiControllerPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final GogAuthService authService;

    @GetMapping(ApiControllerPaths.API + "auth")
    public RefreshTokenResponse authenticate(@RequestParam("code") String code) {
        authService.authenticate(code);
        return RefreshTokenResponse.of(authService.getRefreshToken());
    }

    @GetMapping(ApiControllerPaths.API + "refresh")
    public RefreshTokenResponse refresh(@RequestParam("refresh_token") String refreshToken) {
        authService.refresh(refreshToken);
        return RefreshTokenResponse.of(authService.getRefreshToken());
    }
}
