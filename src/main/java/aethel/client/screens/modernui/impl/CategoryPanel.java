package aethel.client.screens.modernui.impl;

import net.minecraft.client.input.MouseButtonEvent;
import org.lwjgl.glfw.GLFW;
import aethel.client.api.module.ModuleCategory;
import aethel.client.utils.math.MathUtils;
import aethel.client.utils.render.animation.modernfx.Decelerate;
import aethel.client.utils.render.animation.modernfx.Direction;
import aethel.client.utils.render.ui.Render2D;
import aethel.client.utils.render.ui.font.FontType;

import java.awt.Color;

public final class CategoryPanel {
    private final Decelerate[] categoryAnimations = new Decelerate[ModuleCategory.values().length];
    private final Decelerate headerTransition = MathUtils.createAnimation(333, Direction.FORWARDS);
    private static ModuleCategory persistedCategory = ModuleCategory.COMBAT;
    private ModuleCategory previousCategory;
    private ModuleCategory selectedCategory = persistedCategory;

    private static final float CATEGORY_START_Y = 70.0F;
    private static final float CATEGORY_STEP_Y = 22.5F;

    // Smoothly eased selection indicator + hover alpha per category.
    private final float[] hoverAlphas = new float[ModuleCategory.values().length];
    private float indicatorProgress;
    private boolean indicatorInitialized;

    public CategoryPanel() {
        int categoryAnimationMs = 220;
        for (ModuleCategory category : ModuleCategory.values()) {
            Direction direction = category == selectedCategory ? Direction.FORWARDS : Direction.BACKWARDS;
            categoryAnimations[category.ordinal()] = MathUtils.createAnimation(categoryAnimationMs, direction);
        }
    }

    public void render(float x, float y, double mouseX, double mouseY) {
        ModuleCategory[] categories = ModuleCategory.values();

        // Smooth the selection indicator towards the active category row.
        float target = selectedCategory().ordinal();
        if (!indicatorInitialized) {
            indicatorProgress = target;
            indicatorInitialized = true;
        } else {
            indicatorProgress += (target - indicatorProgress) * 0.28F;
            float distance = Math.abs(target - indicatorProgress);
            if (distance < 0.004F) {
                indicatorProgress = target;
            }
        }

        float indicatorY = y + CATEGORY_START_Y + indicatorProgress * CATEGORY_STEP_Y;
        // Rounded accent pill that slides between categories.
        Render2D.rect(
                x + 9.0F,
                indicatorY + 3.0F,
                1.6F,
                8.0F,
                0.8F,
                new Color(180, 125, 255, 170).getRGB(),
                new Color(180, 125, 255, 170).getRGB(),
                new Color(195, 135, 195, 190).getRGB(),
                new Color(180, 125, 255, 170).getRGB()
        );

        for (int i = 0; i < categories.length; i++) {
            ModuleCategory category = categories[i];
            boolean hovered = mouseX >= x + 7.5F
                    && mouseX <= x + 7.5F + 41.0F
                    && mouseY >= y + CATEGORY_START_Y + i * CATEGORY_STEP_Y
                    && mouseY <= y + CATEGORY_START_Y + i * CATEGORY_STEP_Y + 14.0F;
            if (hovered) {
                hoverAlphas[i] += (1.0F - hoverAlphas[i]) * 0.24F;
            } else {
                hoverAlphas[i] += (0.0F - hoverAlphas[i]) * 0.24F;
                if (hoverAlphas[i] < 0.002F) {
                    hoverAlphas[i] = 0.0F;
                }
            }
            renderCategoryItem(x, y + CATEGORY_START_Y + i * CATEGORY_STEP_Y, category, categoryProgress(category), hoverAlphas[i]);
        }
    }

    public boolean mouseClicked(MouseButtonEvent event, float x, float y) {
        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return false;
        }
        ModuleCategory category = categoryAt(event.x(), event.y(), x, y);
        if (category == null) {
            return false;
        }
        selectCategory(category);
        return true;
    }

    public ModuleCategory previousCategory() {
        return previousCategory;
    }

    public ModuleCategory selectedCategory() {
        return selectedCategory;
    }

    public float headerProgress() {
        return MathUtils.clamp(headerTransition.getOutput().floatValue(), 0.0F, 1.0F);
    }

    private void renderCategoryItem(float x, float itemY, ModuleCategory category, float animation, float hover) {
        int base = Math.round(200.0F - 40.0F * hover);
        float selection = Math.max(animation, hover * 0.6F);
        int colorValue = Math.round(base + 55.0F * selection);
        int color = new Color(colorValue, colorValue, Math.round(colorValue + 6.0F * hover), Math.round(200.0F + 55.0F * selection)).getRGB();
        float iconX = x + 22.5F;

        Render2D.text(FontType.MAINMENUSCREEN, category.icon(), iconX, itemY, 10F, color);
    }

    private float categoryProgress(ModuleCategory category) {
        return MathUtils.clamp(categoryAnimations[category.ordinal()].getOutput().floatValue(), 0.0F, 1.0F);
    }

    public boolean selectCategory(ModuleCategory category) {
        if (category == null || category == selectedCategory) {
            return false;
        }

        previousCategory = selectedCategory;
        selectedCategory = category;
        persistedCategory = category;
        headerTransition.setDirection(Direction.FORWARDS);
        headerTransition.reset();

        for (ModuleCategory entry : ModuleCategory.values()) {
            categoryAnimations[entry.ordinal()].setDirection(entry == selectedCategory ? Direction.FORWARDS : Direction.BACKWARDS);
        }
        return true;
    }

    private ModuleCategory categoryAt(double mouseX, double mouseY, float x, float y) {
        float categoryPanelX = 7.5F;
        float categoryPanelWidth = 83.0F;
        float categoryStartY = 70.0F;
        float categoryStepY = 22.5F;
        float categoryHitHeight = 14.0F;
        if (mouseX < x + categoryPanelX || mouseX > x + categoryPanelX + categoryPanelWidth) {
            return null;
        }

        ModuleCategory[] categories = ModuleCategory.values();
        for (int i = 0; i < categories.length; i++) {
            float categoryY = y + categoryStartY + i * categoryStepY;
            if (mouseY >= categoryY && mouseY <= categoryY + categoryHitHeight) {
                return categories[i];
            }
        }
        return null;
    }
}
