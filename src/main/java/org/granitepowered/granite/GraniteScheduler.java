package org.granitepowered.granite;

import com.google.common.base.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.api.service.scheduler.Scheduler;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import java.util.Collection;
import java.util.UUID;

@NonnullByDefault
public class GraniteScheduler implements Scheduler {

    @Override
    public Optional<Task> runTask(Object o, Runnable runnable) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Task> runTaskAfter(Object o, Runnable runnable, long l) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Task> runRepeatingTask(Object o, Runnable runnable, long l) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Task> runRepeatingTaskAfter(Object o, Runnable runnable, long l, long l1) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Task> getTaskById(UUID uuid) {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Task> getScheduledTasks() {
        throw new NotImplementedException("");
    }

    @Override
    public Collection<Task> getScheduledTasks(Object o) {
        throw new NotImplementedException("");
    }
}
