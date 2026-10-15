package aethel.client.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import aethel.client.screens.modernui.impl.WorldAnimation;
import aethel.client.utils.render.ui.blur.BlurFramebuffer;
import aethel.client.utils.render.ui.glass.GlassRenderer;
import aethel.client.utils.render.ui.image.ImageRenderer;
import aethel.client.utils.render.ui.outline.outline360.Outline360Renderer;
import aethel.client.utils.render.ui.outline.outlinedefault.DefaultOutlineRenderer;
import aethel.client.utils.render.ui.outline.outlineglass.GlassOutlineRenderer;
import aethel.client.utils.render.ui.arc.ArcOutlineRenderer;
import aethel.client.utils.render.ui.arc.ArcRenderer;
import aethel.client.utils.render.ui.rectangle.rectdefault.DefaultRectangleRenderer;
import aethel.client.utils.render.ui.rectangle.recthalficon.HalfIconRectangleRenderer;
import aethel.client.utils.render.ui.rectangle.recthalftone.HalftoneRectangleRenderer;
import aethel.client.utils.render.ui.ripple.RippleRenderer;
import aethel.client.utils.render.ui.zippy.ZippyRenderer;
import aethel.client.utils.render.item.RenderItem;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {
    private RenderPass aethel$currentRenderPass;
    private boolean aethel$blurDrawActive;
    private boolean aethel$glassDrawActive;
    private boolean aethel$glassOutlineDrawActive;
    private boolean aethel$rectangleDrawActive;
    private boolean aethel$halfIconRectangleDrawActive;
    private boolean aethel$halftoneRectangleDrawActive;
    private boolean aethel$zippyDrawActive;
    private boolean aethel$arcDrawActive;
    private boolean aethel$arcOutlineDrawActive;
    private boolean aethel$outlineDrawActive;
    private boolean aethel$outline360DrawActive;
    private boolean aethel$imageDrawActive;
    private boolean aethel$itemDrawActive;
    private boolean aethel$rippleDrawActive;

    @Redirect(method = "draw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lcom/mojang/blaze3d/ProjectionType;)V", ordinal = 0))
    private void aethel$useModernWorldProjection(GpuBufferSlice projection, ProjectionType projectionType) {
        GpuBufferSlice override = WorldAnimation.projectionOverride();
        if (override != null) {
            RenderSystem.setProjectionMatrix(override, ProjectionType.PERSPECTIVE);
            return;
        }
        RenderSystem.setProjectionMatrix(projection, projectionType);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void aethel$beginBlurFrame(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        BlurFramebuffer.getInstance().beginGuiFrame();
        GlassRenderer.getInstance().beginGuiFrame();
        GlassOutlineRenderer.getInstance().beginGuiFrame();
        DefaultRectangleRenderer.getInstance().beginGuiFrame();
        HalfIconRectangleRenderer.getInstance().beginGuiFrame();
        HalftoneRectangleRenderer.getInstance().beginGuiFrame();
        ZippyRenderer.getInstance().beginGuiFrame();
        ArcRenderer.getInstance().beginGuiFrame();
        ArcOutlineRenderer.getInstance().beginGuiFrame();
        DefaultOutlineRenderer.getInstance().beginGuiFrame();
        Outline360Renderer.getInstance().beginGuiFrame();
        ImageRenderer.getInstance().beginGuiFrame();
        RippleRenderer.getInstance().beginGuiFrame();
        RenderItem.beginGuiFrame();
    }

    @Inject(method = "prepare", at = @At("HEAD"))
    private void aethel$preparePendingBlurResources(CallbackInfo ci) {
        BlurFramebuffer.getInstance().preparePending();
    }

    @Inject(method = "prepare", at = @At("RETURN"))
    private void aethel$prepareRenderUniforms(CallbackInfo ci) {
        BlurFramebuffer.getInstance().prepareBuffers();
        GlassRenderer.getInstance().prepareBuffers();
        GlassOutlineRenderer.getInstance().prepareBuffers();
        DefaultRectangleRenderer.getInstance().prepareBuffers();
        HalfIconRectangleRenderer.getInstance().prepareBuffers();
        HalftoneRectangleRenderer.getInstance().prepareBuffers();
        ZippyRenderer.getInstance().prepareBuffers();
        ArcRenderer.getInstance().prepareBuffers();
        ArcOutlineRenderer.getInstance().prepareBuffers();
        DefaultOutlineRenderer.getInstance().prepareBuffers();
        Outline360Renderer.getInstance().prepareBuffers();
        ImageRenderer.getInstance().prepareBuffers();
        RippleRenderer.getInstance().prepareBuffers();
        RenderItem.prepareBuffers();
    }

    @Inject(method = "draw", at = @At("HEAD"))
    private void aethel$prepareBlurCapture(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        BlurFramebuffer.getInstance().prepareGuiDraw();
    }

    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;processBlurEffect()V", shift = At.Shift.BEFORE))
    private void aethel$prepareBlurCaptureAfterBeforeBlur(GpuBufferSlice fogBuffer, CallbackInfo ci) {
        BlurFramebuffer.getInstance().prepareGuiDraw();
    }

    @Redirect(method = "executeDraw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V"))
    private void aethel$trackPipeline(RenderPass renderPass, RenderPipeline pipeline) {
        aethel$currentRenderPass = renderPass;
        aethel$blurDrawActive = BlurFramebuffer.getInstance().isBlurPipeline(pipeline);
        aethel$glassDrawActive = GlassRenderer.getInstance().isGlassPipeline(pipeline);
        aethel$glassOutlineDrawActive = GlassOutlineRenderer.getInstance().isGlassOutlinePipeline(pipeline);
        aethel$rectangleDrawActive = DefaultRectangleRenderer.getInstance().isRectanglePipeline(pipeline);
        aethel$halfIconRectangleDrawActive = HalfIconRectangleRenderer.getInstance().isHalfIconRectanglePipeline(pipeline);
        aethel$halftoneRectangleDrawActive = HalftoneRectangleRenderer.getInstance().isHalftoneRectanglePipeline(pipeline);
        aethel$zippyDrawActive = ZippyRenderer.getInstance().isZippyPipeline(pipeline);
        aethel$arcDrawActive = ArcRenderer.getInstance().isArcPipeline(pipeline);
        aethel$arcOutlineDrawActive = ArcOutlineRenderer.getInstance().isArcOutlinePipeline(pipeline);
        aethel$outlineDrawActive = DefaultOutlineRenderer.getInstance().isOutlinePipeline(pipeline);
        aethel$outline360DrawActive = Outline360Renderer.getInstance().isOutline360Pipeline(pipeline);
        aethel$imageDrawActive = ImageRenderer.getInstance().isImagePipeline(pipeline);
        aethel$rippleDrawActive = RippleRenderer.getInstance().isRipplePipeline(pipeline);
        aethel$itemDrawActive = RenderItem.isItemPipeline(pipeline);
        renderPass.setPipeline(pipeline);
    }

    @Inject(method = "executeDraw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V", shift = At.Shift.BEFORE))
    private void aethel$bindBlurParams(@Coerce Object draw, RenderPass renderPass, GpuBuffer gpuBuffer, VertexFormat.IndexType indexType, CallbackInfo ci) {
        if (aethel$blurDrawActive && aethel$currentRenderPass != null) {
            BlurFramebuffer.getInstance().bindBlurParams(aethel$currentRenderPass);
        }
        if (aethel$glassDrawActive && aethel$currentRenderPass != null) {
            GlassRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$glassOutlineDrawActive && aethel$currentRenderPass != null) {
            GlassOutlineRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$rectangleDrawActive && aethel$currentRenderPass != null) {
            DefaultRectangleRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$halfIconRectangleDrawActive && aethel$currentRenderPass != null) {
            HalfIconRectangleRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$halftoneRectangleDrawActive && aethel$currentRenderPass != null) {
            HalftoneRectangleRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$zippyDrawActive && aethel$currentRenderPass != null) {
            ZippyRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$arcDrawActive && aethel$currentRenderPass != null) {
            ArcRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$arcOutlineDrawActive && aethel$currentRenderPass != null) {
            ArcOutlineRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$outlineDrawActive && aethel$currentRenderPass != null) {
            DefaultOutlineRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$outline360DrawActive && aethel$currentRenderPass != null) {
            Outline360Renderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$imageDrawActive && aethel$currentRenderPass != null) {
            ImageRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$rippleDrawActive && aethel$currentRenderPass != null) {
            RippleRenderer.getInstance().bindParams(aethel$currentRenderPass);
        }
        if (aethel$itemDrawActive && aethel$currentRenderPass != null) {
            RenderItem.bindParams(aethel$currentRenderPass);
        }
    }

    @Inject(method = "executeDraw", at = @At("RETURN"))
    private void aethel$clearTrackedPipeline(@Coerce Object draw, RenderPass renderPass, GpuBuffer gpuBuffer, VertexFormat.IndexType indexType, CallbackInfo ci) {
        aethel$currentRenderPass = null;
        aethel$blurDrawActive = false;
        aethel$glassDrawActive = false;
        aethel$glassOutlineDrawActive = false;
        aethel$rectangleDrawActive = false;
        aethel$halfIconRectangleDrawActive = false;
        aethel$halftoneRectangleDrawActive = false;
        aethel$zippyDrawActive = false;
        aethel$arcDrawActive = false;
        aethel$arcOutlineDrawActive = false;
        aethel$outlineDrawActive = false;
        aethel$outline360DrawActive = false;
        aethel$imageDrawActive = false;
        aethel$itemDrawActive = false;
        aethel$rippleDrawActive = false;
    }
}
