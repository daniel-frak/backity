package dev.codesoapbox.backity.e2e.backend;

import com.microsoft.playwright.Response;
import dev.codesoapbox.backity.e2e.CustomOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BackendApiAssertion {

    public static Dsl.Method forResource(String url, Response response) {
        String prefixedUrl = Dsl.URL_PREFIX + url;

        return new Dsl(prefixedUrl, response).new Method();
    }

    public static class Dsl {

        private static final String URL_PREFIX = CustomOptions.BASE_URL + "/api";

        private final String url;
        private final Response response;
        private final List<Supplier<Boolean>> assertions = new ArrayList<>();

        private Dsl(String url, Response response) {
            this.url = url;
            this.response = response;
        }

        public class Method {

            public Dsl.Status isGet() {
                assertions.add(() -> response.request().method().equals("GET"));
                return Dsl.this.new Status();
            }

            public Dsl.Status isPost() {
                assertions.add(() -> response.request().method().equals("POST"));
                return Dsl.this.new Status();
            }

            public Dsl.Status isDelete() {
                assertions.add(() -> response.request().method().equals("DELETE"));
                return Dsl.this.new Status();
            }
        }

        public class Status {

            public boolean isSuccessful() {
                assertions.add(() -> response.status() >= 200 && response.status() < 300);
                return Dsl.this.verify();
            }

            public boolean isNoContent() {
                assertions.add(() -> response.status() == 204);
                return Dsl.this.verify();
            }
        }

        private boolean verify() {
            return response.url().startsWith(url)
                    && assertionsAreCorrect();
        }

        private boolean assertionsAreCorrect() {
            return assertions.stream()
                    .allMatch(Supplier::get);
        }
    }
}
