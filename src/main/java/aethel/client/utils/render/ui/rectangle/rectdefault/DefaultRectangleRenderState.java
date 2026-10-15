package aethel.client.utils.render.ui.rectangle.rectdefault;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import aethel.client.utils.render.ui.UiPipelineCompat;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;

final class DefaultRectangleRenderState implements GuiElementRenderState {
    private final BuiltRectangle rectangle;
    private final Matrix3x2f pose;
    private final ScreenRectangle scissorArea;
    private final ScreenRectangle bounds;

    DefaultRectangleRenderState(Matrix3x2f pose, BuiltRectangle rectangle, ScreenRectangle scissorArea) {
        this.rectangle = rectangle;
        this.pose = pose;
        this.scissorArea = scissorArea;
        ScreenRectangle transformedBounds = new ScreenRectangle(
                Math.round(rectangle.x()),
                Math.round(rectangle.y()),
                Math.round(rectangle.width()),
                Math.round(rectangle.height())
        ).transformMaxBounds(this.pose);
        this.bounds = scissorArea == null ? transformedBounds : scissorArea.intersection(transformedBounds);
    }

    @Override
    public void buildVertices(VertexConsumer consumer) {
        if (UiPipelineCompat.active()) {
            // Vulkan backend: vanilla pipeline expects position+color vertices.
            vertexColored(consumer, rectangle.x(), rectangle.y(), rectangle.colorTopLeft());
            vertexColored(consumer, rectangle.x(), rectangle.y() + rectangle.height(), rectangle.colorBottomLeft());
            vertexColored(consumer, rectangle.x() + rectangle.width(), rectangle.y() + rectangle.height(), rectangle.colorBottomRight());
            vertexColored(consumer, rectangle.x() + rectangle.width(), rectangle.y(), rectangle.colorTopRight());
            return;
        }

        if (!DefaultRectangleRenderer.getInstance().reserve(rectangle)) {
            return;
        }

        float x0 = rectangle.x();
        float y0 = rectangle.y();
        float x1 = rectangle.x() + rectangle.width();
        float y1 = rectangle.y() + rectangle.height();

        vertex(consumer, x0, y0);
        vertex(consumer, x0, y1);
        vertex(consumer, x1, y1);
        vertex(consumer, x1, y0);
    }

    private void vertexColored(VertexConsumer consumer, float x, float y, int color) {
        consumer.addVertexWith2DPose(pose, x, y).setColor(color);
    }

    private void vertex(VertexConsumer consumer, float x, float y) {
        consumer.addVertexWith2DPose(pose, x, y);
    }

    @Override
    public RenderPipeline pipeline() {
        return UiPipelineCompat.active() ? UiPipelineCompat.fillFallbackPipeline() : DefaultRectangleRenderer.RECTANGLE_PIPELINE;
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public ScreenRectangle scissorArea() {
        return scissorArea;
    }

    @Override
    public ScreenRectangle bounds() {
        return bounds;
    }
}
