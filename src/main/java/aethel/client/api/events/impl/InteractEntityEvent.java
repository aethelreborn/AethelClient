package aethel.client.api.events.impl;

import net.minecraft.world.entity.Entity;
import aethel.client.api.events.CancellableEvent;

public final class InteractEntityEvent extends CancellableEvent {
    private final Entity entity;

    public InteractEntityEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
