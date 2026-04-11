package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;

public class BackupTargetsApi extends BackendApi {

    public BackupTargetsApi() {
        super("/backup-targets");
    }

    public boolean isCreated(Response response) {
        return response.url().contains(url)
                && response.request().method().equals("POST")
                && isSuccessful(response);
    }

    public boolean isDeleted(Response response) {
        return response.url().contains(url)
                && response.request().method().equals("DELETE")
                && isSuccessful(response);
    }
}
