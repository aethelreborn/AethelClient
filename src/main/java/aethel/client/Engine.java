package aethel.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import aethel.client.manager.Manager;
import aethel.client.utils.render.ui.Render2D;
import aethel.client.utils.sounds.SoundManager;

public class Engine implements ModInitializer, ClientModInitializer {

    private final Manager manager = new Manager();

    @Override
    public void onInitialize() {
        SoundManager.init();
        Render2D.init();
    }

    @Override
    public void onInitializeClient() {
        manager.initClient();
    }
}
