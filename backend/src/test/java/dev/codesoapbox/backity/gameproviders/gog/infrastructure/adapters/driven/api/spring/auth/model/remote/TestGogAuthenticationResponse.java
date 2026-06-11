package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.model.remote;

import lombok.Builder;

@Builder(builderClassName = "Builder", builderMethodName = "", buildMethodName = "internalBuild", setterPrefix = "with")
public class TestGogAuthenticationResponse {

    @lombok.Builder.Default
    private String accessToken = "someAccessToken";

    @lombok.Builder.Default
    private String refreshToken = "someRefreshToken";

    @lombok.Builder.Default
    private Integer expiresIn = 3600;

    @lombok.Builder.Default
    private String sessionId = "someSessionId";

    @lombok.Builder.Default
    private String userId = "someUserId";

    public static GogAuthenticationResponse valid() {
        return validBuilder().build();
    }

    public static Builder validBuilder() {
        return new Builder();
    }

    public static class Builder {

        public GogAuthenticationResponse build() {
            TestGogAuthenticationResponse temp = internalBuild();

            return new GogAuthenticationResponse(
                    temp.accessToken,
                    temp.refreshToken,
                    temp.expiresIn,
                    temp.sessionId,
                    temp.userId
            );
        }
    }
}