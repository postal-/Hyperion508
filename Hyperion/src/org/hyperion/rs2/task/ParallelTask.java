package org.hyperion.rs2.task;

import org.hyperion.rs2.GameEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ParallelTask implements Task {

    /**
     * The child tasks.
     */
    private final Collection<Task> tasks;

    /**
     * Creates the parallel task.
     *
     * @param tasks The child tasks.
     */
    public ParallelTask(Task... tasks) {
        final List<Task> taskList = new ArrayList<Task>();
        for (final Task task : tasks) {
            taskList.add(task);
        }
        this.tasks = Collections.unmodifiableCollection(taskList);
    }

    @Override
    public void execute(final GameEngine context) {
        for (final Task task : tasks) {
            context.submitTask(new Runnable() {

                @Override
                public void run() {
                    task.execute(context);
                }
            });
        }
        try {
            context.waitForPendingParallelTasks();
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
