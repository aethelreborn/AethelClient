package aethel.client.api.module.impl.visual;

import net.minecraft.client.Minecraft;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.NumberSetting;

/**
 * FullBright raises the video gamma during night or dark caves without
 * any resource packs or brightness hacks.
 */
public final class FullBright extends Module {
    private final NumberSetting brightness = register(new NumberSetting("Brightness", "Gamma to use while the module is active.", 16.0D, 1.0D, 30.0D, 0.5D));
    private final BooleanSetting restore = register(new BooleanSetting("Restore Gamma", "Restore previous gamma when disabled.", true));

    private double previousGamma = -1.0D;

    public FullBright() {
        super("FullBright", "Lightens dark areas with a gamma boost.", ModuleCategory.VISUAL);
    }

    @Override
    protected void onEnable() {
        Minecraft client = mc;
        if (client.options == null) {
            return;
        }
        previousGamma = client.options.gamma().get();
        applyGamma();
    }

    @Override
    protected void onDisable() {
        Minecraft client = mc;
        if (client.options == null) {
            return;
        }
        if (restore.getValue() && previousGamma > 0.0D) {
            setGamma(previousGamma);
        }
        previousGamma = -1.0D;
    }

    @Override
    public void onTick(Minecraft client) {
        if (client.options == null) {
            return;
        }
        if (client.options.gamma().get() < brightness.getValue() && client.player != null) {
            applyGamma();
        }
    }

    private void applyGamma() {
        setGamma(brightness.getValue());
    }

    private void setGamma(double value) {
        mc.options.gamma().set(value);
    }
}
