package aethel.client.api.module.impl.combat;

import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;

public class NoInteract extends Module {
    public NoInteract() {
        super("No Interact", "Blocks unwanted interactions.", ModuleCategory.COMBAT);
    }
}
