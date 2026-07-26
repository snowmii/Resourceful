package me.snowmii.resourceful.ui;

import net.minecraft.client.gui.Font;
//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

final class IconButton extends Button {
    private final Font font;
    private final int color;
    private final boolean renderBackground;

    IconButton(final Font font, final Component icon, final int color, final OnPress onPress) {
        this(font, icon, color, false, onPress);
    }

    IconButton(final Font font, final Component icon, final int color, final boolean renderBackground, final OnPress onPress) {
        super(0, 0, 20, 20, icon, onPress, DEFAULT_NARRATION);
        this.font = font;
        this.color = color;
        this.renderBackground = renderBackground;
    }

    @Override
    //? if >=26 {
    protected void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTick) {
        if (this.renderBackground) {
            extractDefaultSprite(graphics);
        }
        int displayColor = isHoveredOrFocused() ? this.color : (this.color & 0x00FFFFFF) | 0xB0000000;
        graphics.centeredText(this.font, getMessage(), getX() + getWidth() / 2, getY() + 6, displayColor);
    }
    //?} else {
    /*protected void renderContents(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
        if (this.renderBackground) {
            renderDefaultSprite(graphics);
        }
        int displayColor = isHoveredOrFocused() ? this.color : (this.color & 0x00FFFFFF) | 0xB0000000;
        graphics.drawCenteredString(this.font, getMessage(), getX() + getWidth() / 2, getY() + 6, displayColor);
    }
    *///?}
}
