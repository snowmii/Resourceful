package me.snowmii.resourceful.mixin;

import me.snowmii.resourceful.Resourceful;
import me.snowmii.resourceful.config.ResourcefulConfig;
import me.snowmii.resourceful.preset.PresetManager;
import me.snowmii.resourceful.screen.PackSelectionScreenAccess;
import me.snowmii.resourceful.ui.PresetPanel;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackSelectionScreen.class)
public abstract class PackSelectionScreenMixin extends Screen implements PackSelectionScreenAccess {
    @Shadow
    @Final
    private PackSelectionModel model;

    @Shadow
    private @Nullable TransferableSelectionList availablePackList;

    @Shadow
    private @Nullable TransferableSelectionList selectedPackList;

    @Unique
    private boolean resourceful$resourcePackScreen;

    @Unique
    private @Nullable PresetManager resourceful$presetManager;

    @Unique
    private @Nullable PresetPanel resourceful$presetPanel;

    @Unique
    private @Nullable String resourceful$selectedPreset;

    protected PackSelectionScreenMixin(final Component title) {
        super(title);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void resourceful$identifyResourcePackScreen(
        final PackRepository repository,
        final Consumer<PackRepository> output,
        final Path packDir,
        final Component title,
        final CallbackInfo ci
    ) {
        this.resourceful$resourcePackScreen = repository == Minecraft.getInstance().getResourcePackRepository();
        this.resourceful$selectedPreset = ResourcefulConfig.INSTANCE.lastSelectedPreset();
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void resourceful$addPresetPanel(final CallbackInfo ci) {
        if (!this.resourceful$resourcePackScreen || this.availablePackList == null || this.selectedPackList == null) {
            return;
        }
        if (this.resourceful$presetManager == null) {
            this.resourceful$presetManager = new PresetManager(Resourceful.configFile("presets.json"));
        }
        int x = this.availablePackList.getX();
        int width = this.selectedPackList.getRight() - x;
        int y = this.availablePackList.getY() - PresetPanel.HEIGHT;
        this.resourceful$presetPanel = new PresetPanel(
            this.font,
            x,
            y,
            width,
            this.resourceful$presetManager,
            this.model,
            this,
            this.availablePackList,
            this.selectedPackList,
            this.resourceful$selectedPreset == null ? "" : this.resourceful$selectedPreset,
            this::addRenderableWidget,
            this::resourceful$selectPreset
        );
    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    private void resourceful$positionPresetPanel(final CallbackInfo ci) {
        if (!this.resourceful$resourcePackScreen || this.availablePackList == null || this.selectedPackList == null) {
            return;
        }

        int availableHeight = Math.max(0, this.availablePackList.getHeight() - PresetPanel.HEIGHT);
        int selectedHeight = Math.max(0, this.selectedPackList.getHeight() - PresetPanel.HEIGHT);
        this.availablePackList.updateSizeAndPosition(
            this.availablePackList.getWidth(),
            availableHeight,
            this.availablePackList.getX(),
            this.availablePackList.getY() + PresetPanel.HEIGHT
        );
        this.selectedPackList.updateSizeAndPosition(
            this.selectedPackList.getWidth(),
            selectedHeight,
            this.selectedPackList.getX(),
            this.selectedPackList.getY() + PresetPanel.HEIGHT
        );

        if (this.resourceful$presetPanel != null) {
            int x = this.availablePackList.getX();
            int width = this.selectedPackList.getRight() - x;
            this.resourceful$presetPanel.reposition(x, this.availablePackList.getY() - PresetPanel.HEIGHT, width);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void resourceful$tickPresetPanel(final CallbackInfo ci) {
        if (this.resourceful$presetPanel != null) {
            this.resourceful$presetPanel.tick();
        }
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        if (this.resourceful$presetPanel != null) {
            this.resourceful$presetPanel.closePresetListIfOutside(event.x(), event.y());
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Unique
    private void resourceful$selectPreset(final String name) {
        this.resourceful$selectedPreset = name;
        ResourcefulConfig.INSTANCE.setLastSelectedPreset(name);
    }

    @Override
    public boolean resourceful$isResourcePackScreen() {
        return this.resourceful$resourcePackScreen;
    }

    @Override
    public TransferableSelectionList resourceful$getAvailablePackList() {
        return this.availablePackList;
    }

    @Override
    public TransferableSelectionList resourceful$getSelectedPackList() {
        return this.selectedPackList;
    }
}
