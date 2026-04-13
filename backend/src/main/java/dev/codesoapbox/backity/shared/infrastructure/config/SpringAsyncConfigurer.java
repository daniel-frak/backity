package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringAsyncConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
@SpringAsyncConfiguration
@Slf4j
@SuppressWarnings("java:S103") // Can't avoid long URL in javadoc
public class SpringAsyncConfigurer implements AsyncConfigurer {

    /// > When an @Async method has a Future-typed return value, it is easy to manage an exception that was thrown
    /// > during the method execution, as this exception is thrown when calling get on the Future result.
    /// > With a void return type, however, the exception is uncaught and cannot be transmitted.
    /// > You can provide an AsyncUncaughtExceptionHandler to handle such exceptions.
    ///
    /// \- [Spring docs](https://docs.spring.io/spring-framework/reference/integration/scheduling.html#scheduling-annotation-support-exception)
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, _) ->
                log.error("Unhandled exception in async listener [{}.{}]",
                        method.getDeclaringClass().getSimpleName(), method.getName(), ex);
    }

    /// The default async executor is [SimpleAsyncTaskExecutor], which creates a new thread on each call.
    /// Since thread creation is not cheap in Java, we override it here.
    @Bean
    public Executor taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
