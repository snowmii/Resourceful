package me.snowmii.resourceful.mixin;

import me.snowmii.resourceful.ui.drag.DragState;
import me.snowmii.resourceful.mixin.accessor.PackEntryAccessor;
import me.snowmii.resourceful.mixin.accessor.TransferableSelectionListAccessor;
import me.snowmii.resourceful.screen.PackSelectionScreenAccess;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerWidget.class)
public abstract class TransferableSelectionListMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void resourceful$armDrag(
        final MouseButtonEvent event,
        final boolean doubleClick,
        final CallbackInfoReturnable<Boolean> cir
    ) {
        TransferableSelectionList self = self();
        if (self == null) {
            return;
        }
        if (event.button() != 0 || !access(self).resourceful$isResourcePackScreen()) {
            DragState.INSTANCE.clear();
            return;
        }

        for (TransferableSelectionList.Entry child : self.children()) {
            if (child instanceof TransferableSelectionList.PackEntry packEntry
                && packEntry.isMouseOver(event.x(), event.y())
                && event.x() >= packEntry.getContentX() + TransferableSelectionList.PackEntry.ICON_SIZE) {
                PackSelectionModel.Entry modelEntry = ((PackEntryAccessor)packEntry).resourceful$getPack();
                DragState.INSTANCE.arm(self, packEntry, modelEntry, event.x(), event.y());
                return;
            }
        }
        DragState.INSTANCE.clear();
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void resourceful$drag(
        final MouseButtonEvent event,
        final double dx,
        final double dy,
        final CallbackInfoReturnable<Boolean> cir
    ) {
        TransferableSelectionList self = self();
        if (self == null || event.button() != 0 || !DragState.INSTANCE.belongsTo(self)) {
            return;
        }

        if (DragState.INSTANCE.update(available(self), selected(self), event.x(), event.y())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void resourceful$drop(final MouseButtonEvent event, final CallbackInfoReturnable<Boolean> cir) {
        TransferableSelectionList self = self();
        if (self == null || event.button() != 0 || !DragState.INSTANCE.belongsTo(self)) {
            return;
        }

        if (DragState.INSTANCE.isDragging()) {
            DragState.INSTANCE.drop(available(self), selected(self));
            cir.setReturnValue(true);
        } else {
            DragState.INSTANCE.clear();
        }
    }

    private TransferableSelectionList self() {
        Object self = this;
        return self instanceof TransferableSelectionList list ? list : null;
    }

    private static PackSelectionScreenAccess access(final TransferableSelectionList list) {
        PackSelectionScreen screen = ((TransferableSelectionListAccessor)list).resourceful$getScreen();
        return (PackSelectionScreenAccess)screen;
    }

    private static TransferableSelectionList available(final TransferableSelectionList list) {
        return access(list).resourceful$getAvailablePackList();
    }

    private static TransferableSelectionList selected(final TransferableSelectionList list) {
        return access(list).resourceful$getSelectedPackList();
    }
}
