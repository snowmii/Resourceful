package me.snowmii.resourceful.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.snowmii.resourceful.config.ResourcefulConfig;
import me.snowmii.resourceful.ui.drag.DragState;
import me.snowmii.resourceful.mixin.accessor.PackEntryAccessor;
//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TransferableSelectionList.PackEntry.class)
public abstract class PackEntryMixin {
    //? if >=26 {
    @Inject(method = "extractContent", at = @At("HEAD"), cancellable = true)
    //?} else
    /*@Inject(method = "renderContent", at = @At("HEAD"), cancellable = true)*/
    private void resourceful$hideDraggedEntry(final CallbackInfo ci) {
        TransferableSelectionList.PackEntry self = (TransferableSelectionList.PackEntry)(Object)this;
        PackSelectionModel.Entry pack = ((PackEntryAccessor)self).resourceful$getPack();
        if (DragState.INSTANCE.isDraggedEntry(pack)) {
            ci.cancel();
        } else {
            // AbstractSelectionList assigns the row's final Y immediately before
            // extracting its content. Applying the gap here avoids fighting mods
            // that animate the list's scroll position during rendering.
            DragState.INSTANCE.applyVisualOffset(self);
        }
    }

    //? if >=26 {
    @Inject(method = "extractContent", at = @At("TAIL"))
    //?} else
    /*@Inject(method = "renderContent", at = @At("TAIL"))*/
    private void resourceful$restoreDragOffset(final CallbackInfo ci) {
        TransferableSelectionList.PackEntry self = (TransferableSelectionList.PackEntry)(Object)this;
        DragState.INSTANCE.restoreVisualOffset(self);
    }

    //? if >=26 {
    @Redirect(
        method = "extractContent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"
        )
    )
    //?} else {
    /*@Redirect(
        method = "renderContent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V"
        )
    )
    *///?}
    private void resourceful$hideMoveArrows(
        //? if >=26 {
        final GuiGraphicsExtractor graphics,
        //?} else
        /*final GuiGraphics graphics,*/
        final RenderPipeline pipeline,
        final Identifier sprite,
        final int x,
        final int y,
        final int width,
        final int height
    ) {
        if (!ResourcefulConfig.INSTANCE.hideArrows() || !sprite.toString().contains("/move_")) {
            graphics.blitSprite(pipeline, sprite, x, y, width, height);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void resourceful$disableHiddenArrowClicks(
        final MouseButtonEvent event,
        final boolean doubleClick,
        final CallbackInfoReturnable<Boolean> cir
    ) {
        if (!ResourcefulConfig.INSTANCE.hideArrows()) {
            return;
        }
        TransferableSelectionList.PackEntry self = (TransferableSelectionList.PackEntry)(Object)this;
        PackSelectionModel.Entry pack = ((PackEntryAccessor)self).resourceful$getPack();
        int relativeX = (int)event.x() - self.getContentX();
        int relativeY = (int)event.y() - self.getContentY();
        if (!pack.canSelect() && relativeX >= 16 && relativeX < 32 && relativeY >= 0 && relativeY < 32) {
            cir.setReturnValue(false);
        }
    }
}
