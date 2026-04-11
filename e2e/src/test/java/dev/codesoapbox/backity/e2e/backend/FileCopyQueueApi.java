package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;

public class FileCopyQueueApi {

    private static final String URL = "/file-copy-queue";

    public boolean isEnqueued(Response response) {
        return BackendApiAssertion.forResource(URL, response)
                .isPost()
                .isSuccessful();
    }

    public boolean isCanceled(Response response) {
        return BackendApiAssertion.forResource(URL, response)
                .isDelete()
                .isSuccessful();
    }
}
