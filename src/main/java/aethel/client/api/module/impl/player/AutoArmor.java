package aethel.client.api.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.utils.inventory.InventoryTask;

/**
 * Vape-style AutoArmor: equips the best armor pieces straight from the
 * inventory, replacing strictly worse ones — no GUI clicks needed.
 */
public final class AutoArmor extends Module {
    private final BooleanSetting replaceWorse = register(new BooleanSetting("Replace Worse", "Swap out armor that is worse than the best pickup.", false));
    private final BooleanSetting noInventoryMove = register(new BooleanSetting("Pause In GUI", "Do not act while a container is open.", true));

    private int cooldown;

    public AutoArmor() {
        super("AutoArmor", "Equips the best armor from the inventory automatically.", ModuleCategory.PLAYER);
    }

    @Override
    public void onTick(Minecraft client) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        if (client.player == null || client.gameMode == null) {
            return;
        }
        if (noInventoryMove.getValue() && (client.player.containerMenu != client.player.inventoryMenu || client.screen != null)) {
            return;
        }

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack current = client.player.getItemBySlot(slot);
            int bestScore = replaceWorse.getValue()
                    ? armorScore(current)
                    : (current.isEmpty() ? -1 : armorScore(current));
            int menuSlot = armorMenuSlot(slot);
            if (menuSlot < 0) {
                continue;
            }

            int bestInvSlot = -1;
            Inventory inventory = client.player.getInventory();
            for (int i = 0; i < 36; i++) {
                ItemStack stack = inventory.getItem(i);
                if (!isMatchingEquippable(stack, slot)) {
                    continue;
                }
                int score = armorScore(stack);
                if (score > bestScore) {
                    bestScore = score;
                    bestInvSlot = i;
                }
            }

            if (bestInvSlot >= 0) {
                // Moves the best pickup into the armor slot (menu-slot id space).
                InventoryTask.moveItem(toMenuSlot(bestInvSlot), menuSlot, false, false);
                cooldown = 4;
                return;
            }
        }
    }

    /**
     * Converts an inventory index (0-35) into an InventoryMenu slot id:
     * 0-26 -> 9-35 (main rows), 27-35 -> 36-44 (hotbar).
     */
    private int toMenuSlot(int inventoryIndex) {
        return inventoryIndex < 27 ? inventoryIndex + 9 : inventoryIndex + 9;
    }

    /**
     * Menu slot id of the given armor slot inside InventoryMenu:
     * 5 = head, 6 = chest, 7 = legs, 8 = feet.
     */
    private int armorMenuSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> 5;
            case CHEST -> 6;
            case LEGS -> 7;
            case FEET -> 8;
            default -> -1;
        };
    }

    private boolean isMatchingEquippable(ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) {
            return false;
        }
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot() == slot;
    }

    private int armorScore(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }
        double armor = componentAttributeValue(stack, Attributes.ARMOR);
        double toughness = componentAttributeValue(stack, Attributes.ARMOR_TOUGHNESS);
        return (int) Math.round(armor * 10.0D + toughness);
    }

    private double componentAttributeValue(ItemStack stack, Holder<Attribute> attribute) {
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) {
            return 0.0D;
        }
        double total = 0.0D;
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().value() == attribute.value()) {
                total += entry.modifier().amount();
            }
        }
        return total;
    }
}
