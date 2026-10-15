package aethel.client.api.events.impl;

import aethel.client.api.events.Event;
import aethel.client.api.module.Module;

public final class ModuleToggleEvent implements Event {
    private final Module module;
    private final boolean enabled;

    public ModuleToggleEvent(Module module, boolean enabled) {
        this.module = module;
        this.enabled = enabled;
    }

    public Module getModule() {
        return module;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
