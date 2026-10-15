package aethel.client.screens.modernui.settings;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;
import aethel.client.api.settings.impl.BooleanSetting;
import aethel.client.utils.render.animation.modernfx.Direction;
import aethel.client.utils.render.ui.Render2D;
import aethel.client.utils.render.ui.font.FontType;

import java.awt.Color;

final class BooleanSettingCard extends SettingCardComponent<BooleanSetting> {
    BooleanSettingCard(BooleanSetting setting) {
        super(setting);
    }

    @Override
    int order() {
        return 3;
    }

    @Override
    float height(SettingCardContext context) {
        return 14.0F + descriptionOffset(context, 102.0F);
    }

    @Override
    void render(GuiGraphics graphics, SettingCardContext context, float cardX, float y, int mouseX, int mouseY) {
        markRow(cardX, y, height(context));
        state.booleanAnimation.setDirection(setting.getValue() ? Direction.FORWARDS : Direction.BACKWARDS);
        float animation = context.clamp(state.booleanAnimation.getOutput().floatValue(), 0.0F, 1.0F);
        float boxX = cardX + 127.0F;
        float boxY = y + 1;
        state.box(boxX, boxY, 12.5F, 12.5F);

        renderNameAndDescription(graphics, context, cardX + 10.0F, y, 102.0F, 102.0F);
        Render2D.rect(boxX, boxY, 12.5f, 12.5f, 2f,
                context.color(185, 185, 185, 50),
                context.color(185, 185, 185, 50),
                context.color(185, 185, 185, 50),
                context.color(185, 185, 185, 50));
        if (animation > 0.01F) {
            Render2D.rect(boxX, boxY, 12.5f, 12.5f, 2f,
                    context.withAlpha(new Color(145, 115, 185, 250), animation),
                    context.withAlpha(new Color(195, 135, 195, 250), animation),
                    context.withAlpha(new Color(145, 115, 185, 250), animation),
                    context.withAlpha(new Color(125, 105, 145, 250), animation));
        }

        // Cross fades out, check fades in with the toggle animation.
        int crossAlpha = Math.round(175.0F - 85.0F * animation);
        Render2D.text(FontType.MAINMENUSCREEN, "Z", boxX - 10f, boxY + 3, 8, context.color(200, 200, 205, Math.max(90, crossAlpha)));

        int checkAlpha = Math.round(120.0F + 135.0F * animation);
        Render2D.text(
                FontType.MAINMENUSCREEN,
                "F",
                boxX + 2.5f,
                boxY + 3,
                8,
                context.color(
                        Math.round(210.0F + 45.0F * animation),
                        Math.round(195.0F + 60.0F * animation),
                        255,
                        checkAlpha));

    }

    @Override
    boolean mouseClicked(MouseButtonEvent event, boolean doubled, SettingCardContext context) {
        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT || !context.inside(event.x(), event.y(), state.rowX, state.rowY - 4.0F, state.rowWidth, state.rowHeight + 4.0F)) {
            return false;
        }
        setting.setValue(!setting.getValue());
        context.activeColor = null;
        return true;
    }
}
