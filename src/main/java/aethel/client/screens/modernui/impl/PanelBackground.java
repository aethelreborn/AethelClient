package aethel.client.screens.modernui.impl;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import aethel.client.api.module.Module;
import aethel.client.api.module.ModuleCategory;
import aethel.client.screens.modernui.GuiTheme;
import aethel.client.screens.modernui.settings.SettingCardController;
import aethel.client.utils.render.ui.Render2D;
import aethel.client.utils.render.ui.font.FontType;

import java.awt.Color;
import java.util.List;

public final class PanelBackground {
    private final SettingCardController settings = new SettingCardController();

    public void render(GuiGraphics graphics, float x, float y, float panelWidth, float panelHeight, ModuleCategory category, boolean searchMode, List<Module> modules, int mouseX, int mouseY) {
        renderShell(x, y, panelWidth, panelHeight);
        settings.render(graphics, x, y, panelWidth, panelHeight, category, searchMode, modules, mouseX, mouseY);
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubled) {
        return settings.mouseClicked(event, doubled);
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        return settings.mouseReleased(event);
    }

    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return settings.mouseDragged(event, dragX, dragY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return settings.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public boolean keyPressed(KeyEvent event) {
        return settings.keyPressed(event);
    }

    public boolean charTyped(CharacterEvent event) {
        return settings.charTyped(event);
    }

    private void renderShell(float x, float y, float panelWidth, float panelHeight) {
        Render2D.blur(
                x,
                y - 0.5f,
                panelWidth,
                panelHeight,
                11,
                50.0F,
                1.5F,
                GuiTheme.NAVY_DEEP,
                GuiTheme.NAVY_DEEP,
                GuiTheme.NAVY_DEEP,
                GuiTheme.NAVY_DEEP
        );

        Render2D.liquidGlass(x,
                y - 0.5f,
                panelWidth,
                panelHeight, 2.0f, 0.075f, 11, new Color(150, 190, 255, 255).getRGB());

        Render2D.rect(x,
                y - 0.5f,
                panelWidth,
                panelHeight, 11, new Color(8, 13, 28, 115).getRGB());

        // Accent sheen along the top edge of the glass panel.
        float sheen = panelWidth - 30.0F;
        Render2D.rect(
                x + (panelWidth - sheen) * 0.5F,
                y + 4.5F,
                sheen,
                0.6F,
                0,
                GuiTheme.rgb(90, 145, 255, 0),
                GuiTheme.rgb(90, 145, 255, 0),
                GuiTheme.rgb(90, 145, 255, 110),
                GuiTheme.rgb(90, 145, 255, 110)
        );

        // Frosted sidebar rail for categories.
        Render2D.rect(
                x + 7.5F,
                y + 7.5F,
                40F,
                panelHeight - 15.0F,
                8,
                GuiTheme.rgb(140, 170, 235, 40),
                GuiTheme.rgb(140, 170, 235, 40),
                GuiTheme.rgb(35, 52, 100, 55),
                GuiTheme.rgb(35, 52, 100, 55)
        );

        Render2D.outline(
                x + 7.5F,
                y + 7.5F,
                40F,
                panelHeight - 15.0F,
                8,
                0.25f,
                GuiTheme.rgb(170, 195, 255, 85),
                GuiTheme.rgb(170, 195, 255, 85),
                GuiTheme.rgb(70, 100, 180, 95),
                GuiTheme.rgb(70, 100, 180, 95)
        );

        Render2D.rect(
                x + 15F,
                y + 15F,
                25F,
                25F,
                5,
                GuiTheme.rgb(110, 140, 220, 60)
        );

        Render2D.outline(
                x + 15F,
                y + 15F,
                25F,
                25F,
                5,
                0.15f,
                new Color(255, 255, 255, 220).getRGB(),
                new Color(255, 255, 255, 25).getRGB(),
                new Color(255, 255, 255, 220).getRGB(),
                new Color(255, 255, 255, 25).getRGB()
        );

        float logoSize = 18.0F;
        float logoWidth = Render2D.textWidth(FontType.MAINMENUSCREEN, "f", logoSize);
        Render2D.text(
                FontType.MAINMENUSCREEN,
                "f",
                x + 15F + (25F - logoWidth) * 0.5F,
                y + 16F + (25F - logoSize) * 0.5F,
                logoSize,
                new Color(255, 255, 255, 255).getRGB()
        );

        // Module list surface.
        Render2D.rect(
                x + 53.0F, y + 41.5F, panelWidth - 60.0F, panelHeight - 49.0F,
                6,
                GuiTheme.rgb(145, 175, 235, 50),
                GuiTheme.rgb(145, 175, 235, 50),
                GuiTheme.rgb(38, 56, 104, 60),
                GuiTheme.rgb(38, 56, 104, 60)
        );

        Render2D.outline(
                x + 53.0F, y + 41.5F, panelWidth - 60.0F, panelHeight - 49.0F,
                6,
                0.25f,
                GuiTheme.rgb(170, 195, 255, 85),
                GuiTheme.rgb(170, 195, 255, 85),
                GuiTheme.rgb(70, 100, 180, 95),
                GuiTheme.rgb(70, 100, 180, 95)
        );

        // Top strip with the category title + search.
        Render2D.rect(
                x + 53.0F,
                y + 7.5F,
                panelWidth - 60.0F,
                30,
                6,
                GuiTheme.rgb(145, 175, 235, 50),
                GuiTheme.rgb(145, 175, 235, 50),
                GuiTheme.rgb(145, 175, 235, 50),
                GuiTheme.rgb(145, 175, 235, 50)
        );

        Render2D.outline(
                x + 53.0F,
                y + 7.5F,
                panelWidth - 60.0F,
                30,
                6,
                0.25f,
                GuiTheme.rgb(170, 195, 255, 85),
                GuiTheme.rgb(170, 195, 255, 85),
                GuiTheme.rgb(170, 195, 255, 85),
                GuiTheme.rgb(170, 195, 255, 85)
        );
    }
}
