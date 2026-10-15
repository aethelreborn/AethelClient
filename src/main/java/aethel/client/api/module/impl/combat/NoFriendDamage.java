package aethel.client.api.module.impl.combat;

import aethel.client.api.events.annotation.SubscribeEvent;
import aethel.client.api.events.impl.InteractEntityEvent;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.utils.repository.friend.FriendUtils;

public class NoFriendDamage extends Module {
    public NoFriendDamage() {
        super("No Friend Damage", "Blocks attacks on friends.", ModuleCategory.COMBAT);
    }

    @SubscribeEvent
    private void onInteract(InteractEntityEvent event) {
        if (FriendUtils.isFriend(event.getEntity())) {
            event.cancel();
        }
    }
}
