package aethel.client.utils.render.ui.rectangle.recthalftone;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import aethel.client.utils.render.ui.UiPipelineCompat;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;

final class HalftoneRectangleRenderState implements GuiElementRenderState {
    private final BuiltHalftoneRectangle rectangle;
    private final Matrix3x2f pose;
    private final ScreenRectangle bounds;

    HalftoneRectangleRenderState(Matrix3x2f pose, BuiltHalftoneRectangle rectangle) {
        this.rectangle = rectangle;
        this.pose = pose;
        this.bounds = new ScreenRectangle(
                Math.round(rectangle.x()),
                Math.round(rectangle.y()),
                Math.round(rectangle.width()),
                Math.round(rectangle.height())
        ).transformMaxBounds(this.pose);
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

        int batchIndex = HalftoneRectangleRenderer.getInstance().reserve(rectangle);
        if (batchIndex < 0) {
            return;
        }

        float x0 = rectangle.x();
        float y0 = rectangle.y();
        float x1 = rectangle.x() + rectangle.width();
        float y1 = rectangle.y() + rectangle.height();

        vertex(consumer, x0, y0, 0, 0, batchIndex);
        vertex(consumer, x0, y1, 0, 255, batchIndex);
        vertex(consumer, x1, y1, 255, 255, batchIndex);
        vertex(consumer, x1, y0, 255, 0, batchIndex);
    }

    private void vertexColored(VertexConsumer consumer, float x, float y, int color) {
        consumer.addVertexWith2DPose(pose, x, y).setColor(color);
    }

    private void vertex(VertexConsumer consumer, float x, float y, int coordX, int coordY, int batchIndex) {
        consumer.addVertexWith2DPose(pose, x, y)
                .setColor(coordX, coordY, 255, 255)
                .setLineWidth((float) (batchIndex + 1));
    }

    @Override
    public RenderPipeline pipeline() {
        return UiPipelineCompat.active() ? UiPipelineCompat.fillFallbackPipeline() : HalftoneRectangleRenderer.HALFTONE_RECTANGLE_PIPELINE;
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public ScreenRectangle scissorArea() {
        return null;
    }

    @Override
    public ScreenRectangle bounds() {
        return bounds;
    }
}
