package me.snowmii.resourceful.ui;

//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;

final class DropdownFrame extends AbstractWidget {
    DropdownFrame() {
        super(0, 0, 0, 0, CommonComponents.EMPTY);
        this.active = false;
        this.visible = false;
    }

    @Override
    //? if >=26 {
    protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTick) {
        graphics.fill(getX(), getY(), getRight(), getBottom(), 0xFF8A8A8A);
        graphics.fill(getX() + 1, getY() + 1, getRight() - 1, getBottom() - 1, 0xF0181818);
    }
    //?} else {
    /*protected void renderWidget(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
        graphics.fill(getX(), getY(), getRight(), getBottom(), 0xFF8A8A8A);
        graphics.fill(getX() + 1, getY() + 1, getRight() - 1, getBottom() - 1, 0xF0181818);
    }
    *///?}

    @Override
    protected void updateWidgetNarration(final NarrationElementOutput output) {
    }
}
