package aethel.client.api.events.impl;

import net.minecraft.world.phys.Vec3;
import aethel.client.api.events.Event;

public final class FireworkEvent implements Event {
    private Vec3 vector;

    public FireworkEvent(Vec3 vector) {
        this.vector = vector;
    }

    public Vec3 getVector() {
        return vector;
    }

    public void setVector(Vec3 vector) {
        this.vector = vector;
    }
}
