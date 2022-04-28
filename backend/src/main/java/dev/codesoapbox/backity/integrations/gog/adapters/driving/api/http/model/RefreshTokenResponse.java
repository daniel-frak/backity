package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value(staticConstructor = "of")
public class RefreshTokenResponse {

    @JsonProperty("refresh_token")
    String refreshToken;
}
