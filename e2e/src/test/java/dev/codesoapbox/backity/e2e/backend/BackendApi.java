package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;

public class BackendApi {

    private static final String BASE = "/api";
    protected final String url;

    protected BackendApi(String path) {
        this.url = BASE + path;
    }

    protected boolean isSuccessful(Response response) {
        return response.status() >= 200 && response.status() < 300;
    }
}
