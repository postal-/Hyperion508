package org.hyperion.rs2.tickable.event.impl;

import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.task.ConsecutiveTask;
import org.hyperion.rs2.task.ParallelTask;
import org.hyperion.rs2.task.Task;
import org.hyperion.rs2.task.impl.*;
import org.hyperion.rs2.tickable.Tickable;
import org.hyperion.rs2.tickable.event.Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Linux
 */
public class UpdateEvent extends Event {

    /**
     * The cycle time, in milliseconds.
     */
    public static final int CYCLE_TIME = 600;

    /**
     * Creates the update event to cycle every 600 milliseconds.
     */
    public UpdateEvent() {
        super(CYCLE_TIME);
    }

    @Override
    public void execute() {
        Iterator<Tickable> tickIt$ = World.getWorld().getTickableManager().getTickables().iterator();
        while (tickIt$.hasNext()) {
            Tickable t = tickIt$.next();
            t.cycle();
            if (!t.isRunning()) {
                tickIt$.remove();
            }
        }

        final List<Task> tickTasks = new ArrayList<Task>();
        final List<Task> updateTasks = new ArrayList<Task>();
        final List<Task> resetTasks = new ArrayList<Task>();

        for (final NPC npc : World.getWorld().getNPCs()) {
            tickTasks.add(new NPCTickTask(npc));
            resetTasks.add(new NPCResetTask(npc));
        }

        final Iterator<Player> it$ = World.getWorld().getPlayers().iterator();
        while (it$.hasNext()) {
            final Player player = it$.next();
            if (!player.getSession().isConnected()) {
                it$.remove();
            } else {
                tickTasks.add(new PlayerTickTask(player));
                updateTasks.add(new ConsecutiveTask(new PlayerUpdateTask(player), new NPCUpdateTask(player)));
                resetTasks.add(new PlayerResetTask(player));
            }
        }

        // ticks can no longer be parallel due to region code
        final Task tickTask = new ConsecutiveTask(tickTasks.toArray(new Task[0]));
        final Task updateTask = new ParallelTask(updateTasks.toArray(new Task[0]));
        final Task resetTask = new ParallelTask(resetTasks.toArray(new Task[0]));

        World.getWorld().submit(new ConsecutiveTask(tickTask, updateTask, resetTask));
    }
}