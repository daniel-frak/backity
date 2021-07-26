package dev.codesoapbox.gogbackupservice.gog.application.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value(staticConstructor = "of")
public class RefreshTokenResponse {

    @JsonProperty("refresh_token")
    String refreshToken;
}
