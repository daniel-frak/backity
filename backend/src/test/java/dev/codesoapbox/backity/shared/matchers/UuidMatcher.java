package dev.codesoapbox.backity.shared.matchers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UuidMatcher extends TypeSafeMatcher<String> {

    public static final String UUID_REGEX = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}";

    public static UuidMatcher isUuid() {
        return new UuidMatcher();
    }

    @Override
    protected boolean matchesSafely(String s) {
        return s.matches(UUID_REGEX);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a string matching the pattern of a UUID");
    }
}
