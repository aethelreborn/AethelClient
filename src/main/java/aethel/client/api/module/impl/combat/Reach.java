package aethel.client.api.module.impl.combat;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.NumberSetting;

/**
 * Precise reach module: adds a transient attribute modifier to the vanilla
 * entity/block interaction ranges so extension stays consistent with
 * vanilla calculations (no raytracing hacks).
 */
public final class Reach extends Module {
    public static final ResourceLocation REACH_ID = ResourceLocation.fromNamespaceAndPath("aethelclient", "reach");

    private final NumberSetting entityRange = register(new NumberSetting("Entity Distance", "Extra reach towards entities.", 0.5D, 0.0D, 2.5D, 0.1D));
    private final NumberSetting blockRange = register(new NumberSetting("Block Distance", "Extra reach for block interaction.", 0.5D, 0.0D, 2.5D, 0.1D));
    private final BooleanSetting onlyEntities = register(new BooleanSetting("Entity Only", "Do not extend block interaction range.", false));

    public Reach() {
        super("Reach", "Extends vanilla interaction range via attribute modifiers.", ModuleCategory.COMBAT);
    }

    @Override
    public void onTick(net.minecraft.client.Minecraft client) {
        apply();
    }

    @Override
    protected void onEnable() {
        apply();
    }

    @Override
    protected void onDisable() {
        remove();
    }

    private void apply() {
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        update(player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE), entityRange.getValue());
        if (!onlyEntities.getValue()) {
            update(player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE), blockRange.getValue());
        }
    }

    private void update(AttributeInstance attribute, double amount) {
        if (attribute == null) {
            return;
        }
        AttributeModifier existing = attribute.getModifier(REACH_ID);
        if (amount <= 0.0D) {
            if (existing != null) {
                attribute.removeModifier(REACH_ID);
            }
            return;
        }
        if (existing != null && Math.abs(existing.amount() - amount) < 0.001D) {
            return;
        }
        if (existing != null) {
            attribute.removeModifier(REACH_ID);
        }
        attribute.addTransientModifier(new AttributeModifier(REACH_ID, amount, AttributeModifier.Operation.ADD_VALUE));
    }

    private void remove() {
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        AttributeInstance entity = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (entity != null) {
            entity.removeModifier(REACH_ID);
        }
        AttributeInstance block = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (block != null) {
            block.removeModifier(REACH_ID);
        }
    }
}
