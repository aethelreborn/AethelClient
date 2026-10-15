package aethel.client.api.events.impl;

import aethel.client.api.events.CancellableEvent;

public final class HotBarScrollEvent extends CancellableEvent {
    private final double horizontal;
    private final double vertical;

    public HotBarScrollEvent(double horizontal, double vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public double getHorizontal() {
        return horizontal;
    }

    public double getVertical() {
        return vertical;
    }
}
