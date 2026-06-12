package dev.codesoapbox.backity.testing.scheduling;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.util.ClassUtils;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
public class RegisteredSchedulers {

    private final ScheduledTaskHolder taskHolder;

    public void execute(ScheduledMethod scheduledMethod) {
        taskHolder.getScheduledTasks().stream()
                .map(scheduledTask -> scheduledTask.getTask().getRunnable())
                .filter(runnable -> runnable.toString().equals(qualifiedName(scheduledMethod)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Scheduler %s is not registered. Registered schedulers: %s"
                                .formatted(scheduledMethod, scheduledMethods())))
                .run();
    }

    private String qualifiedName(ScheduledMethod scheduledMethod) {
        return "%s.%s".formatted(scheduledMethod.schedulerClass().getName(), scheduledMethod.methodName());
    }

    public Set<ScheduledMethod> scheduledMethods() {
        return taskHolder.getScheduledTasks().stream()
                .map(scheduledTask -> scheduledTask.getTask().getRunnable().toString())
                .map(this::parse)
                .collect(toSet());
    }

    private ScheduledMethod parse(String qualifiedMethodName) {
        int lastDot = qualifiedMethodName.lastIndexOf('.');
        Class<?> schedulerClass =
                ClassUtils.resolveClassName(qualifiedMethodName.substring(0, lastDot), null);

        return new ScheduledMethod(schedulerClass, qualifiedMethodName.substring(lastDot + 1));
    }
}