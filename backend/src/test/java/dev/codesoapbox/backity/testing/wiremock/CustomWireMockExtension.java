package dev.codesoapbox.backity.testing.wiremock;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;

public final class CustomWireMockExtension {

    public static WireMockExtension newInstance() {
        return WireMockExtension.newInstance()
                .options(new WireMockConfiguration()
                        .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.NEVER)
                        .usingFilesUnderClasspath("wiremock")
                        .dynamicPort())
                .build();
    }
}
