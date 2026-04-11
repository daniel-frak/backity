package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;

public class FileCopiesApi {

    private static final String URL = "/file-copies";

    public boolean isDeleted(Response response) {
        return BackendApiAssertion.forResource(URL, response)
                .isDelete()
                .isNoContent();
    }
}
