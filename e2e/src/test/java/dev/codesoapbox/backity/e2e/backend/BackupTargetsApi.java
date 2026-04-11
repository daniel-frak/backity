package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;

public class BackupTargetsApi {

    private static final String URL = "/backup-targets";

    public boolean isCreated(Response response) {
        return BackendApiAssertion.forResource(URL, response)
                .isPost()
                .isSuccessful();
    }

    public boolean isDeleted(Response response) {
        return BackendApiAssertion.forResource(URL, response)
                .isDelete()
                .isSuccessful();
    }
}
