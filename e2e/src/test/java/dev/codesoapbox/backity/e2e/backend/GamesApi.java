package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;

public class GamesApi extends BackendApi {

    public GamesApi() {
        super("/games");
    }

    public boolean retrievedGames(Response response) {
        return response.url().contains(url) && isSuccessful(response);
    }
}
