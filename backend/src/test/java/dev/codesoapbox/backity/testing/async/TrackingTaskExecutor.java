package dev.codesoapbox.backity.testing.async;

import org.springframework.core.task.TaskExecutor;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/// Tracks execution of asynchronous tasks to allow verifying asynchrony.
public class TrackingTaskExecutor implements TaskExecutor {

    private static final ThreadLocal<UUID> currentExecutorClientId = new ThreadLocal<>();

    private final TaskExecutor delegate;
    private final Set<UUID> externalClientIdsUsed = ConcurrentHashMap.newKeySet();

    public TrackingTaskExecutor(TaskExecutor delegate) {
        this.delegate = delegate;
    }

    /// Sets the id of the thread which is calling the executor (**not** the thread in which async execution happens).
    /// Used to ensure the test is asserting on the relevant execution. 
    public void setExecutorClientId(UUID executorClientId) {
        currentExecutorClientId.set(executorClientId);
    }

    public void reset() {
        externalClientIdsUsed.clear();
        currentExecutorClientId.remove();
    }

    @Override
    public void execute(Runnable task) {
        UUID executorClientId = currentExecutorClientId.get();

        if (executorClientId != null) {
            delegate.execute(() -> {
                externalClientIdsUsed.add(executorClientId);
                task.run();
            });
        } else {
            delegate.execute(task);
        }
    }

    public boolean wasUsedFor(UUID executorClientId) {
        return externalClientIdsUsed.contains(executorClientId);
    }
}
