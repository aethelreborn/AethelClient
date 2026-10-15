package aethel.client.api.events.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import aethel.client.api.events.Event;

public record BlockBreakingEvent(BlockPos blockPos, Direction direction) implements Event {
}
