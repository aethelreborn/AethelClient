package aethel.client.api.events.impl;

import net.minecraft.world.phys.Vec3;
import aethel.client.api.events.Event;

public final class MoveEvent implements Event {
    private Vec3 movement;

    public MoveEvent(Vec3 movement) {
        this.movement = movement;
    }

    public Vec3 getMovement() {
        return movement;
    }

    public void setMovement(Vec3 movement) {
        this.movement = movement;
    }
}
