package dev.codesoapbox.backity.testing.scheduling;

public record ScheduledMethod(Class<?> schedulerClass, String methodName) {

    @Override
    public String toString() {
        return "%s#%s".formatted(schedulerClass, methodName);
    }
}
