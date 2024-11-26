package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.openapi.lowercaseenums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CaseInsensitiveEnumConverterTest {

    private CaseInsensitiveEnumConverter<SampleEnum> converter;

    @BeforeEach
    void setUp() {
        converter = new CaseInsensitiveEnumConverter<>(SampleEnum.class);
    }

    @Test
    void shouldReturnEnumValueIgnoringCase() {
        assertThat(converter.convert("value_one"))
                .isEqualTo(SampleEnum.VALUE_ONE);
        assertThat(converter.convert("VALUE_TWO"))
                .isEqualTo(SampleEnum.VALUE_TWO);
        assertThat(converter.convert("ValuE_ThreE"))
                .isEqualTo(SampleEnum.VALUE_THREE);
    }

    @Test
    void shouldThrowExceptionForInvalidEnumValue() {
        assertThatThrownBy(() -> converter.convert("invalid_value"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No enum constant");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldHandleNullInput() {
        assertThatThrownBy(() -> converter.convert(null))
                .isInstanceOf(NullPointerException.class);
    }

    public enum SampleEnum {
        VALUE_ONE,
        VALUE_TWO,
        VALUE_THREE
    }
}