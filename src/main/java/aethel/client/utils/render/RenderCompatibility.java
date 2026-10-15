package aethel.client.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;

public final class RenderCompatibility {
    private static Boolean safeWorldEffects;
    private static String safeWorldEffectsReason = "unresolved";
    private static final String SAFE_WORLD_EFFECTS_PROPERTY = "aethel.safeWorldEffects";
    private static final String SAFE_WORLD_EFFECTS_ENV = "AETHEL_SAFE_WORLD_EFFECTS";

    private static Boolean vulkanCompat;
    private static String vulkanCompatReason = "unresolved";
    private static final String VULKAN_COMPAT_PROPERTY = "aethel.vulkanCompat";
    private static final String VULKAN_COMPAT_ENV = "AETHEL_VULKAN_COMPAT";
    private static final String[] VULKAN_MOD_IDS = {"vulkanmod", "not-enough-vulkan"};

    private static Boolean disableFragEffectScanShader;
    private static String disableFragEffectScanShaderReason = "unresolved";
    private static final String DISABLE_FRAG_EFFECT_SCAN_PROPERTY = "aethel.disableFragEffectScanShader";
    private static final String DISABLE_FRAG_EFFECT_SCAN_ENV = "AETHEL_DISABLE_FRAG_EFFECT_SCAN_SHADER";

    private RenderCompatibility() {
    }

    public static boolean useSafeWorldEffects() {
        Boolean manualOverride = readManualOverride(System.getProperty(SAFE_WORLD_EFFECTS_PROPERTY));
        if (manualOverride == null) {
            manualOverride = readManualOverride(System.getenv(SAFE_WORLD_EFFECTS_ENV));
        }
        if (manualOverride != null) {
            safeWorldEffects = manualOverride;
            safeWorldEffectsReason = "manual override";
            return manualOverride;
        }

        if (safeWorldEffects == null) {
            safeWorldEffects = false;
            safeWorldEffectsReason = "default-path";
        }
        return safeWorldEffects;
    }

    /**
     * Vulkan backends (VulkanMod and friends) replace the GL renderer and only support
     * a strict subset of GLSL via their own shader converter: uniform block fields must be
     * plain scalars/vectors, so array-field uniform blocks (used by the batched Aethel UI
     * pipelines) cannot compile and crash when the pipeline is first used.
     *
     * When this returns true, the batched pipeline renderers fall back to the vanilla
     * GUI / GUI_TEXTURED pipelines instead, which any Vulkan backend can compile.
     */
    public static boolean useVulkanCompatibility() {
        Boolean manualOverride = readManualOverride(System.getProperty(VULKAN_COMPAT_PROPERTY));
        if (manualOverride == null) {
            manualOverride = readManualOverride(System.getenv(VULKAN_COMPAT_ENV));
        }
        if (manualOverride != null) {
            vulkanCompat = manualOverride;
            vulkanCompatReason = "manual override";
            return manualOverride;
        }

        if (vulkanCompat == null) {
            if (hasVulkanModInstalled()) {
                vulkanCompat = true;
                vulkanCompatReason = "vulkan mod installed";
            } else if (isVulkanDevice()) {
                vulkanCompat = true;
                vulkanCompatReason = "vulkan render device detected";
            } else {
                vulkanCompat = false;
                vulkanCompatReason = "no vulkan backend detected";
            }

            if (vulkanCompat) {
                System.out.println("[AethelClient] Vulkan compatibility active (" + vulkanCompatReason + "): custom UI pipelines will use vanilla fallbacks");
            }
        }

        return vulkanCompat;
    }

    public static String getVulkanCompatReason() {
        return vulkanCompatReason;
    }

    private static boolean hasVulkanModInstalled() {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader == null) {
            return false;
        }

        for (String modId : VULKAN_MOD_IDS) {
            if (loader.isModLoaded(modId)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isVulkanDevice() {
        try {
            Object device = RenderSystem.tryGetDevice();
            if (device == null) {
                return false;
            }

            String deviceName = device.getClass().getName();
            String simpleName = device.getClass().getSimpleName();
            return deviceName.contains("vulkanmod") || simpleName.contains("Vulkan") || simpleName.startsWith("Vk");
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void primeFromCurrentContext() {
        useSafeWorldEffects();
        shouldDisableFragEffectScanShader();
        useVulkanCompatibility();
    }

    public static boolean shouldDisableFragEffectScanShader() {
        Boolean manualOverride = readManualOverride(System.getProperty(DISABLE_FRAG_EFFECT_SCAN_PROPERTY));
        if (manualOverride == null) {
            manualOverride = readManualOverride(System.getenv(DISABLE_FRAG_EFFECT_SCAN_ENV));
        }
        if (manualOverride != null) {
            disableFragEffectScanShader = manualOverride;
            disableFragEffectScanShaderReason = "manual override";
            return manualOverride;
        }

        if (disableFragEffectScanShader == null) {
            disableFragEffectScanShader = false;
            disableFragEffectScanShaderReason = "depth-copy shader path enabled";
        }
        return disableFragEffectScanShader;
    }

    public static String getSafeWorldEffectsReason() {
        return safeWorldEffectsReason;
    }

    public static String getDisableFragEffectScanShaderReason() {
        return disableFragEffectScanShaderReason;
    }

    private static Boolean readManualOverride(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toLowerCase();
        if (normalized.equals("1") || normalized.equals("true") || normalized.equals("yes") || normalized.equals("on")) {
            return true;
        }
        if (normalized.equals("0") || normalized.equals("false") || normalized.equals("no") || normalized.equals("off")) {
            return false;
        }
        return null;
    }
}
