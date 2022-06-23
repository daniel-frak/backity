package dev.codesoapbox.backity.core.shared.config.openapi;

import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2-test")
class OpenApiAdditionalSchemaProviderTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCustomise() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "SpecialTestObject\":{\"type\":\"string\",\"enum\":[\"SPECIAL_TEST_VALUE\"]}"
                )));
    }

    @TestConfiguration
    public static class OpenApiTestConfig {
        @Bean
        @ConditionalOnMissingBean
        public OpenApiCustomiser addAdditionalClasses(Reflections reflections) {
            return new OpenApiAdditionalSchemaProvider(reflections);
        }

        @IncludeInOpenApiDocs
        public enum SpecialTestObject {
            SPECIAL_TEST_VALUE
        }
    }
}