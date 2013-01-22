package org.hyperion.rs2.action.impl;

import org.hyperion.rs2.action.Action;
import org.hyperion.rs2.model.*;

public abstract class HarvestingAction extends Action {

    /**
     * The location.
     */
    private final Location location;

    /**
     * Creates the harvesting action for the specified player.
     *
     * @param player The player to create the action for.
     */
    public HarvestingAction(Player player, Location location) {
        super(player, 0);
        this.location = location;
    }

    @Override
    public QueuePolicy getQueuePolicy() {
        return QueuePolicy.NEVER;
    }

    @Override
    public WalkablePolicy getWalkablePolicy() {
        return WalkablePolicy.NON_WALKABLE;
    }

    /**
     * Called when the action is initialized.
     */
    public abstract void init();

    /**
     * Gets the harvest delay.
     *
     * @return The delay between consecutive harvests.
     */
    public abstract long getHarvestDelay();

    /**
     * Gets the number of cycles.
     *
     * @return The number of cycles.
     */
    public abstract int getCycles();

    /**
     * Gets the success factor.
     *
     * @return The success factor.
     */
    public abstract double getFactor();

    /**
     * Gets the harvested item.
     *
     * @return The harvested item.
     */
    public abstract Item getHarvestedItem();

    /**
     * Gets the experience.
     *
     * @return The experience.
     */
    public abstract double getExperience();

    /**
     * Gets the skill.
     *
     * @return The skill.
     */
    public abstract int getSkill();

    /**
     * Gets the animation.
     *
     * @return The animation.
     */
    public abstract Animation getAnimation();

    /**
     * Gets reward type.
     *
     * @return <code>true/false</code> Whether items are rewarded periodically
     * during the action.
     */
    public abstract boolean getPeriodicRewards();
    /**
     * The total number of cycles.
     */
    private int totalCycles;
    /**
     * The number of remaining cycles.
     */
    private int cycles;

    /**
     * Grants the player his or her reward.
     *
     * @param player The player object.
     * @param reward The item reward object.
     */
    private void giveRewards(Player player, Item reward) {
        player.getInventory().add(reward);
        final ItemDefinition def = reward.getDefinition();
        player.getActionSender().sendMessage(
                "You get some " + def.getName() + ".");
        player.getSkills().addExperience(getSkill(), getExperience());
    }

    @Override
    public void execute() {
        final Player player = getPlayer();
        if (getDelay() == 0) {
            setDelay(getHarvestDelay());
            init();
            if (isRunning()) {
                player.playAnimation(getAnimation());
                player.face(location);
            }
            cycles = getCycles();
            totalCycles = cycles;
        } else {
            cycles--;
            final Item item = getHarvestedItem();
            if (player.getInventory().hasRoomFor(item)) {
                if (totalCycles == 1 || Math.random() > getFactor()) {
                    if (getPeriodicRewards()) {
                        giveRewards(player, item);
                    }
                }
            } else {
                stop();
                player.getActionSender().sendMessage(
                        "There is not enough space in your inventory.");
                return;
            }
            if (cycles == 0) {
                // TODO replace with expired object!
                if (!getPeriodicRewards()) {
                    giveRewards(player, item);
                }
                stop();
            } else {
                player.playAnimation(getAnimation());
                player.face(location);
            }
        }
    }
}
