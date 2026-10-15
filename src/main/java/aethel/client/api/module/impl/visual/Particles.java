package aethel.client.api.module.impl.visual;

import net.minecraft.client.Minecraft;
import aethel.client.api.events.annotation.SubscribeEvent;
import aethel.client.api.events.impl.AttackEvent;
import aethel.client.api.events.impl.WorldRenderEvent;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.Setting;
import aethel.client.api.module.impl.visual.particles.ParticleSettings;
import aethel.client.api.module.impl.visual.particles.ParticleSystem;

public class Particles extends Module {
    private final ParticleSettings particleSettings;
    private final ParticleSystem particleSystem;

    public Particles() {
        super("Particles", "Displays Excellent-style world and attack particles.", ModuleCategory.VISUAL);
        this.particleSettings = new ParticleSettings(setting -> registerSetting(setting));
        this.particleSystem = new ParticleSystem(this.particleSettings);
    }

    @Override
    protected void onEnable() {
        particleSystem.clear();
    }

    @Override
    protected void onDisable() {
        particleSystem.clear();
    }

    @Override
    public void onTick(Minecraft client) {
        particleSystem.tick(client);
    }

    @SubscribeEvent
    private void onWorldRender(WorldRenderEvent event) {
        particleSystem.render(mc, event);
    }

    @SubscribeEvent
    private void onAttack(AttackEvent event) {
        particleSystem.spawnAttack(mc, event.getTarget());
    }

    private <S extends Setting<?>> S registerSetting(S setting) {
        return register(setting);
    }
}
