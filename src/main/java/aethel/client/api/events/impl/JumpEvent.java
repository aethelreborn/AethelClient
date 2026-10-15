package aethel.client.api.events.impl;

import net.minecraft.world.entity.player.Player;
import aethel.client.api.events.CancellableEvent;

public final class JumpEvent extends CancellableEvent {
    private final Player player;

    public JumpEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
