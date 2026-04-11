package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;

public class FileCopiesApi extends BackendApi {

    public FileCopiesApi() {
        super("/file-copies");
    }

    public boolean isDeleted(Response response) {
        return response.url().contains(url)
                && response.request().method().equals("DELETE")
                && response.status() == 204;
    }
}
