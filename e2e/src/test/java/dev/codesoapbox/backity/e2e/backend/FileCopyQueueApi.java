package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;

public class FileCopyQueueApi extends BackendApi {

    public FileCopyQueueApi() {
        super("/file-copy-queue");
    }

    public boolean isEnqueued(Response response) {
        return response.url().contains(url)
                && response.request().method().equals("POST")
                && isSuccessful(response);
    }

    public boolean isCanceled(Response response) {
        return response.url().contains(url)
                && response.request().method().equals("DELETE")
                && isSuccessful(response);
    }
}
