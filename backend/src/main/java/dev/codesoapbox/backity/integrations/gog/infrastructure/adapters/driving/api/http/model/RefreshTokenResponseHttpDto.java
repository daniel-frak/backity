package dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.api.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Schema(name = "RefreshTokenResponse")
@Value(staticConstructor = "of")
public class RefreshTokenResponseHttpDto {

    @JsonProperty("refresh_token")
    String refreshToken;
}
