package me.snowmii.resourceful.mixin;

import me.snowmii.resourceful.ui.drag.DragState;
import me.snowmii.resourceful.mixin.accessor.TransferableSelectionListAccessor;
import me.snowmii.resourceful.screen.PackSelectionScreenAccess;
//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin {
    @Inject(method = "contentHeight", at = @At("RETURN"), cancellable = true)
    private void resourceful$adjustDragContentHeight(final CallbackInfoReturnable<Integer> cir) {
        Object self = this;
        if (self instanceof TransferableSelectionList list) {
            cir.setReturnValue(cir.getReturnValue() + DragState.INSTANCE.contentHeightAdjustment(list));
        }
    }

    //? if >=26 {
    @Inject(method = "extractWidgetRenderState", at = @At("HEAD"))
    //?} else
    /*@Inject(method = "renderWidget", at = @At("HEAD"))*/
    private void resourceful$updateDragGap(
        //? if >=26 {
        final GuiGraphicsExtractor graphics,
        //?} else
        /*final GuiGraphics graphics,*/
        final int mouseX,
        final int mouseY,
        final float partialTick,
        final CallbackInfo ci
    ) {
        Object self = this;
        if (self instanceof TransferableSelectionList list) {
            DragState.INSTANCE.updateVisualOffsets(list);
        }
    }

    //? if >=26 {
    @Inject(method = "extractWidgetRenderState", at = @At("TAIL"))
    //?} else
    /*@Inject(method = "renderWidget", at = @At("TAIL"))*/
    private void resourceful$renderDrag(
        //? if >=26 {
        final GuiGraphicsExtractor graphics,
        //?} else
        /*final GuiGraphics graphics,*/
        final int mouseX,
        final int mouseY,
        final float partialTick,
        final CallbackInfo ci
    ) {
        Object self = this;
        if (!(self instanceof TransferableSelectionList list)) {
            return;
        }

        DragState.INSTANCE.renderIndicator(list, graphics);
        if (list == selected(list)) {
            DragState.INSTANCE.renderGhost(list, graphics);
        }
    }

    @Unique
    private static TransferableSelectionList selected(final TransferableSelectionList list) {
        PackSelectionScreen screen = ((TransferableSelectionListAccessor)list).resourceful$getScreen();
        return ((PackSelectionScreenAccess)screen).resourceful$getSelectedPackList();
    }
}
