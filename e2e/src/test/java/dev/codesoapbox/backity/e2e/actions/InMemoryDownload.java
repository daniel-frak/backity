package dev.codesoapbox.backity.e2e.actions;

import com.microsoft.playwright.Download;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InMemoryDownload {

    private final Download download;

    @SneakyThrows
    public String downloadFileAndReadContent() {
        try (
                InputStream inputStream = download.createReadStream();
                var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            return reader.lines()
                    .collect(Collectors.joining("\n"));
        }
    }

    public String suggestedFilename() {
        return download.suggestedFilename();
    }
}
