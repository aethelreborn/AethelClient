package aethel.client.utils.render.ui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import aethel.client.utils.render.RenderCompatibility;
import net.minecraft.client.renderer.RenderPipelines;

/**
 * Central helper for the VulkanMod compatibility path.
 *
 * VulkanMod replaces the GL renderer and converts every shader to SPIR-V with a
 * strictly limited GLSL parser. That parser does not support array fields inside
 * uniform blocks, which the batched Aethel UI pipelines rely on, so any of those
 * pipelines crash on first use ("Caught exception while parsing ..."). One of them
 * (batched_blur) is used during the world-join animation, so the client crashes the
 * moment it joins a server while VulkanMod is installed.
 *
 * While a Vulkan backend is active this helper routes the batched render states to
 * the vanilla GUI / GUI_TEXTURED pipelines (which VulkanMod handles natively) and
 * renders plain colored quads instead of the shader-based shapes.
 */
public final class UiPipelineCompat {

    private UiPipelineCompat() {
    }

    /** True while a Vulkan backend is present and custom GLSL pipelines must be avoided. */
    public static boolean active() {
        return RenderCompatibility.useVulkanCompatibility();
    }

    /** Untextured fill fallback: vanilla <code>gui</code> pipeline (POSITION_COLOR). */
    public static RenderPipeline fillFallbackPipeline() {
        return RenderPipelines.GUI;
    }

    /** Textured fallback: vanilla <code>gui_textured</code> pipeline (POSITION_TEX_COLOR). */
    public static RenderPipeline texturedFallbackPipeline() {
        return RenderPipelines.GUI_TEXTURED;
    }

    /** Scales ARGB color for a fallback fill (e.g. softer substitutes for shader effects). */
    public static int withAlphaScale(int color, float scale) {
        int alpha = (color >>> 24) & 0xFF;
        int scaled = Math.max(0, Math.min(255, Math.round(alpha * scale)));
        return (scaled << 24) | (color & 0x00FFFFFF);
    }
}
