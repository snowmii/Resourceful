package me.snowmii.resourceful.screen;

import net.minecraft.client.gui.screens.packs.TransferableSelectionList;

public interface PackSelectionScreenAccess {
    boolean resourceful$isResourcePackScreen();

    TransferableSelectionList resourceful$getAvailablePackList();

    TransferableSelectionList resourceful$getSelectedPackList();
}
