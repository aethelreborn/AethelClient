package aethel.client.api.module.impl.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.module.impl.combat.aura.Angle;
import aethel.client.api.module.impl.combat.aura.AngleConfig;
import aethel.client.api.module.impl.combat.aura.AngleConnection;
import aethel.client.api.module.impl.combat.aura.MathAngle;
import aethel.client.api.module.impl.combat.aura.rotations.SnapAngle;
import aethel.client.api.module.impl.combat.aura.util.StopWatch;
import aethel.client.api.module.impl.combat.aura.util.TaskPriority;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.utils.inventory.lookup.InventoryUtils;

/**
 * Vape-style Scaffold: bridges and towers for you. Places the held block
 * into the cell your velocity is about to enter, rotating server-side
 * through the aura rotation stack and only firing on placeable targets.
 */
public final class Scaffold extends Module {
    private final NumberSetting placeDelay = register(new NumberSetting("Place Delay", "Milliseconds between placements.", 75.0D, 0.0D, 500.0D, 15.0D));
    private final BooleanSetting tower = register(new BooleanSetting("Tower", "Pillar upward while holding forward on an edge.", true));
    private final BooleanSetting rotate = register(new BooleanSetting("Rotate", "Server-sided rotations via the snap angle system.", true));

    private final StopWatch placeTimer = new StopWatch();

    public Scaffold() {
        super("Scaffold", "Bridges blocks under your path automatically.", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onDisable() {
        Minecraft client = mc;
        if (client.options != null) {
            client.options.keyJump.setDown(false);
        }
    }

    @Override
    public void onTick(Minecraft client) {
        if (client.player == null || client.level == null || client.screen != null || client.options == null) {
            return;
        }

        maybeTower(client);

        boolean offHand = client.player.getOffhandItem().getItem() instanceof BlockItem;
        int slot = offHand ? -1 : findHotbarBlockSlot(client);
        BlockPos target = findPlaceTarget(client);
        if (target.equals(BlockPos.ZERO) || (!offHand && slot == -1)) {
            return;
        }

        ItemStack stack = offHand ? client.player.getOffhandItem() : client.player.getInventory().getItem(slot);
        if (!(stack.getItem() instanceof BlockItem)) {
            return;
        }

        // Skip when the cell already has a solid block we stand in front of.
        if (client.level.getBlockState(target).isCollisionShapeFullBlock(client.level, target)
                || client.level.getBlockState(target.below()).isCollisionShapeFullBlock(client.level, target.below())) {
            return;
        }

        Vec3 hitVec = target.getCenter();
        Direction direction = nearestDirection(
                hitVec.x - client.player.getX(),
                hitVec.y - client.player.getY(),
                hitVec.z - client.player.getZ()
        );

        if (rotate.getValue()) {
            Angle angle = MathAngle.calculateAngle(hitVec.subtract(Vec3.atLowerCornerOf(direction.getUnitVec3i()).scale(0.1F)));
            AngleConnection.INSTANCE.rotateTo(
                    new Angle.VecRotation(angle, angle.toVector()),
                    client.player,
                    1,
                    new AngleConfig(new SnapAngle(), true, true),
                    TaskPriority.HIGH_IMPORTANCE_1,
                    this
            );
        }

        if (!placeTimer.finished(placeDelay.getValue()) || !canPlace(client, (BlockItem) stack.getItem(), target)) {
            return;
        }

        InteractionHand hand = offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        int previous = client.player.getInventory().getSelectedSlot();
        if (!offHand) {
            client.player.getInventory().setSelectedSlot(slot);
            client.player.connection.send(new ServerboundSetCarriedItemPacket(slot));
        }

        BlockHitResult hit = new BlockHitResult(hitVec, direction.getOpposite(), target, false);
        client.gameMode.useItemOn(client.player, hand, hit);
        client.player.connection.send(new ServerboundSwingPacket(hand));

        if (!offHand) {
            InventoryUtils.selectSlot(previous);
            client.player.connection.send(new ServerboundSetCarriedItemPacket(previous));
        }
        placeTimer.reset();
    }

    /**
     * Tower mode: while walking into an edge, jump so the next placement
     * stacks the column under you.
     */
    private void maybeTower(Minecraft client) {
        if (!tower.getValue()
                || !client.options.keyUp.isDown()
                || !client.player.onGround()
                || client.player.getDeltaMovement().horizontalDistanceSqr() < 0.001D) {
            return;
        }
        BlockPos belowFront = client.player.blockPosition().relative(client.player.getDirection()).below();
        if (client.level.getBlockState(belowFront).isAir()) {
            client.options.keyJump.setDown(true);
        }
    }

    private int findHotbarBlockSlot(Minecraft client) {
        for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getItem(i).getItem() instanceof BlockItem) {
                return i;
            }
        }
        return -1;
    }

    /**
     * The cell the player is about to enter one step below flight level —
     * the vanilla "under your next footstep" target.
     */
    private BlockPos findPlaceTarget(Minecraft client) {
        Vec3 predicted = client.player.position()
                .add(client.player.getDeltaMovement())
                .add(0.0D, -1.0E-3D, 0.0D);
        return BlockPos.containing(predicted);
    }

    private boolean canPlace(Minecraft client, BlockItem blockItem, BlockPos target) {
        VoxelShape shape = blockItem.getBlock().defaultBlockState().getCollisionShape(client.level, target);
        if (shape.isEmpty()) {
            return false;
        }
        AABB box = shape.bounds().move(target);
        return !box.intersects(client.player.getBoundingBox());
    }

    private static Direction nearestDirection(double x, double y, double z) {
        double absX = Math.abs(x);
        double absY = Math.abs(y);
        double absZ = Math.abs(z);

        if (absX >= absY && absX >= absZ) {
            return x >= 0.0D ? Direction.EAST : Direction.WEST;
        }
        if (absY >= absX && absY >= absZ) {
            return y >= 0.0D ? Direction.UP : Direction.DOWN;
        }
        return z >= 0.0D ? Direction.SOUTH : Direction.NORTH;
    }
}
