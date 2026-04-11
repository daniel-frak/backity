package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;

public class GamesApi {

    private static final String URL = "/games";

    public boolean retrievedGames(Response response) {
        return BackendApiAssertion.forResource(URL, response)
                .isGet()
                .isSuccessful();
    }
}
