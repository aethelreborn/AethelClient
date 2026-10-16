package aethel.client.api.module.impl.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import aethel.client.api.events.annotation.SubscribeEvent;
import aethel.client.api.events.impl.WorldRenderEvent;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.api.settings.impl.ColorSetting;
import aethel.client.api.settings.impl.MultiModeSetting;
import aethel.client.api.settings.impl.NumberSetting;
import aethel.client.utils.render.Render3D;
import aethel.client.utils.string.chat.ChatMessage;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Vape / Prestige style base finder: scans loaded chunks for
 * man-made marker blocks (chests, shulkers, farms, furnaces...) and
 * highlights them plus logs each unique discovery in chat.
 */
public final class BaseFinder extends Module {
    private static final long FULL_SCAN_INTERVAL_MS = 2000L;

    private final NumberSetting range = register(new NumberSetting("Range", "Search radius in blocks.", 48.0D, 16.0D, 128.0D, 8.0D));
    private final MultiModeSetting markers = register(new MultiModeSetting("Markers", "Man-made block types to search for.",
            new String[]{"Chests", "Shulkers", "Storages", "Furnaces", "Brewing", "Bases & Utility"},
            "Chests", "Shulkers", "Storages", "Furnaces"));
    private final BooleanSetting notifyInChat = register(new BooleanSetting("Notify", "Log each unique find in chat.", true));
    private final ColorSetting color = register(new ColorSetting("Color", "Highlight color.", new Color(255, 191, 73, 120)));

    private final Map<BlockPos, BlockState> foundBases = new HashMap<>();
    private long lastFullScanMs;

    public BaseFinder() {
        super("BaseFinder", "Finds player bases via man-made blocks in loaded chunks.", ModuleCategory.MISC);
    }

    @Override
    protected void onEnable() {
        foundBases.clear();
        lastFullScanMs = 0L;
    }

    @Override
    protected void onDisable() {
        foundBases.clear();
    }

    @Override
    public void onTick(Minecraft client) {
        if (client.level == null || client.player == null) {
            foundBases.clear();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastFullScanMs < FULL_SCAN_INTERVAL_MS) {
            return;
        }
        lastFullScanMs = now;
        scanLoadedChunks(client);
        prune(client);
    }

    private void scanLoadedChunks(Minecraft client) {
        BlockPos playerPos = client.player.blockPosition();
        double maxDistanceSqr = (double) range.getFloat() * range.getFloat();
        int chunkRange = Math.min(8, Math.max(1, (int) Math.ceil(range.getFloat() / 16.0D))) + 1;
        int minYBase = client.level.getMinY();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int x = -chunkRange; x <= chunkRange; x++) {
            for (int z = -chunkRange; z <= chunkRange; z++) {
                int chunkX = (playerPos.getX() >> 4) + x;
                int chunkZ = (playerPos.getZ() >> 4) + z;
                if (!client.level.getChunkSource().hasChunk(chunkX, chunkZ)) {
                    continue;
                }
                LevelChunk chunk = client.level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }

                int cx = chunk.getPos().x << 4;
                int cz = chunk.getPos().z << 4;
                for (int bx = 0; bx < 16; bx++) {
                    for (int bz = 0; bz < 16; bz++) {
                        int columnX = cx + bx;
                        int columnZ = cz + bz;
                        int minY = Math.max(minYBase, playerPos.getY() - 32);
                        int maxY = Math.min(client.level.getHeight(Heightmap.Types.WORLD_SURFACE, columnX, columnZ), playerPos.getY() + 32);
                        for (int by = minY; by <= maxY; by++) {
                            mutablePos.set(columnX, by, columnZ);
                            double dist = client.player.distanceToSqr(columnX + 0.5D, by + 0.5D, columnZ + 0.5D);
                            if (dist > maxDistanceSqr) {
                                continue;
                            }

                            BlockState state = client.level.getBlockState(mutablePos);
                            Block block = state.getBlock();
                            if (!isMarkerBlock(block, markers)) {
                                continue;
                            }

                            BlockPos immutablePos = mutablePos.immutable();
                            if (!foundBases.containsKey(immutablePos)) {
                                foundBases.put(immutablePos, state);
                                if (notifyInChat.getValue()) {
                                    notifyBaseFound(immutablePos, BuiltInRegistries.BLOCK.getKey(block).toString());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void prune(Minecraft client) {
        foundBases.entrySet().removeIf(entry -> !isMarkerBlock(client.level.getBlockState(entry.getKey()).getBlock(), markers));
    }

    private static boolean isMarkerBlock(Block block, MultiModeSetting types) {
        if (types.isSelected("Chests") && block instanceof ChestBlock) {
            return true;
        }
        if (types.isSelected("Shulkers") && block instanceof ShulkerBoxBlock) {
            return true;
        }
        if (types.isSelected("Storages") && (block instanceof BarrelBlock || block instanceof HopperBlock || block instanceof DispenserBlock || block instanceof DropperBlock)) {
            return true;
        }
        if (types.isSelected("Furnaces") && (block instanceof FurnaceBlock || block instanceof BlastFurnaceBlock || block instanceof SmokerBlock)) {
            return true;
        }
        if (types.isSelected("Brewing") && block instanceof net.minecraft.world.level.block.BrewingStandBlock) {
            return true;
        }
        if (types.isSelected("Bases & Utility") && (block instanceof CraftingTableBlock || block instanceof AnvilBlock || block instanceof BeaconBlock)) {
            return true;
        }
        return false;
    }

    @SubscribeEvent
    private void onRender3D(WorldRenderEvent event) {
        if (!isEnabled() || mc.level == null || mc.player == null) {
            return;
        }
        int highlightColor = color.getValue().getRGB();
        for (Map.Entry<BlockPos, BlockState> entry : foundBases.entrySet()) {
            AABB box = new AABB(entry.getKey());
            Render3D.drawBox(box, highlightColor, 1.0f);
            Render3D.drawBoxOverlay(box, highlightColor, 1.25f);
        }
    }

    private void notifyBaseFound(BlockPos pos, String blockName) {
        if (mc.player == null) {
            return;
        }
        mc.player.displayClientMessage(
                ChatMessage.brandmessage().copy()
                        .append(Component.literal(" -> ").withStyle(net.minecraft.ChatFormatting.DARK_GRAY))
                        .append(Component.literal("Base mark " + blockName + " @ " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())),
                false
        );
    }
}
