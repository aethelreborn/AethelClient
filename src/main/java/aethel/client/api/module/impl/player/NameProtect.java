package aethel.client.api.module.impl.player;

import net.minecraft.client.Minecraft;
import aethel.client.api.events.annotation.SubscribeEvent;
import aethel.client.api.events.impl.TextFactoryEvent;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.StringSetting;

public final class NameProtect extends Module {
    private final StringSetting nameSetting = register(new StringSetting("Name", "Replacement nickname.", "aethelclient.dev", 32));
    private final BooleanSetting friendsSetting = register(new BooleanSetting("Friends", "Hide friend nicknames when friend repository is available.", true));

    public NameProtect() {
        super("Name Protect", "Hides your nickname in rendered text.", ModuleCategory.PLAYER);
    }

    @SubscribeEvent
    private void onTextFactory(TextFactoryEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (client.getUser() != null) {
            event.replaceText(client.getUser().getName(), nameSetting.getValue());
        }
    }
}
