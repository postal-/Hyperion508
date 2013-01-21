package org.hyperion.rs2.tickable.impl;

import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.EntityCooldowns.CooldownFlags;
import org.hyperion.rs2.tickable.Tickable;

public class CooldownTick extends Tickable {

    private final Entity entity;
    private final CooldownFlags cooldown;

    /**
     * Creates a cooldown event for a single CooldownFlag.
     *
     * @param entity   The entity for whom we are expiring a cooldown.
     * @param duration The length of the cooldown.
     */
    public CooldownTick(Entity entity, CooldownFlags cooldown, int duration) {
        super(duration);
        this.entity = entity;
        this.cooldown = cooldown;
    }

    @Override
    public void execute() {
        entity.getEntityCooldowns().set(cooldown, false);
        stop();
    }
}
