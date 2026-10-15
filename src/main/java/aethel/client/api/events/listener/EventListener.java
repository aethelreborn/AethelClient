package aethel.client.api.events.listener;

import aethel.client.api.events.Event;

@FunctionalInterface
public interface EventListener<T extends Event> {
    void invoke(T event) throws Exception;
}
