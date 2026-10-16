package aethel.client.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleManager;
import aethel.client.api.module.impl.misc.ClickGuiModule;
import aethel.client.api.settings.bind.KeyBind;
import aethel.client.manager.Manager;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void aethel$toggleClickGui(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (Manager.getInstance() != null && Manager.getInstance().isDestroyed()) {
            return;
        }
        if (event.key() != GLFW.GLFW_KEY_UNKNOWN && window == this.minecraft.getWindow().handle() && this.minecraft.screen == null) {
            Manager.postEvent(new aethel.client.api.events.impl.KeyEvent(this.minecraft.screen, InputConstants.Type.KEYSYM, event.key(), action));
        }
        if (action == GLFW.GLFW_PRESS && matchesClickGuiBind(event.key())) {
            Manager.toggleClickGui(this.minecraft);
            ci.cancel();
        }
    }

    private boolean matchesClickGuiBind(int key) {
        if (key < 0) {
            return false;
        }
        ModuleManager modules = Manager.getModules();
        if (modules == null) {
            return key == GLFW.GLFW_KEY_RIGHT_SHIFT;
        }
        Module clickGui = modules.getByType(ClickGuiModule.class).orElse(null);
        if (clickGui == null) {
            return key == GLFW.GLFW_KEY_RIGHT_SHIFT;
        }
        KeyBind bind = clickGui.getBind();
        return bind.getType() == aethel.client.api.settings.bind.InputType.KEYBOARD && bind.getCode() == key;
    }
}
