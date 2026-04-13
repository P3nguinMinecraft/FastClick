package io.github.penguin.fastclick.mixin;

import io.github.penguin.fastclick.Config;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow private int rightClickDelay;

    @Unique int heldDuration = 0;
    @Inject(method = "tick", at = @At("TAIL"))
    private void inject(CallbackInfo ci) {
        if (Minecraft.getInstance().options.keyUse.isDown()) heldDuration++;
        else heldDuration = 0;
        if (Config.enabled && heldDuration >= Config.delay) rightClickDelay = 0;
    }
}