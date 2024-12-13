/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.codesoapbox.backity.testing;

import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.annotation.*;

/**
 * Temporary replacement for the deprecated @MockBeans, until (hopefully) type-level @MockitoBean support is added.
 *
 * @see <a href="https://github.com/spring-projects/spring-framework/issues/33925">Spring Framework Github issue</a>
 * @see <a href="https://github.com/spring-projects/spring-framework/issues/33934#issuecomment-2514372031">
 *     Github comment about the temporary solution</a>
 */
@SuppressWarnings("removal")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface TemporaryMockBeans {

    /**
     * Return the contained {@link MockBean @MockBean} annotations.
     *
     * @return the mock beans
     */
    TemporaryMockBean[] value();
}
