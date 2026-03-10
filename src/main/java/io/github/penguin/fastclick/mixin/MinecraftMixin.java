package io.github.penguin.fastclick.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.penguin.fastclick.Config;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Final
    @Shadow
    public MouseHandler mouseHandler;

    @Unique
    private int leftDelay = 0;
    @Unique
    private int rightDelay = 0;
    @Unique
    private int middleDelay = 0;

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void repeatMouseClicks(CallbackInfo ci) {
        repeatButton(0, mouseHandler.isLeftPressed(), true);
        repeatButton(1, mouseHandler.isRightPressed(), false);
        repeatButton(2, mouseHandler.isMiddlePressed(), false);
    }

    @Unique
    private void repeatButton(int button, boolean pressed, boolean primary) {
        int delay;

        if (button == 0) delay = leftDelay;
        else if (button == 1) delay = rightDelay;
        else delay = middleDelay;

        if (!pressed) {
            setDelay(button, 0);
            return;
        }

        if (delay == 0) {
            setDelay(button, 10);
            return;
        }

        delay--;

        if (delay <= 0) {
            InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(button);
            if (Config.enabled) KeyMapping.click(key);
            delay = 1;
        }

        setDelay(button, delay);
    }

    @Unique
    private void setDelay(int button, int value) {
        if (button == 0) leftDelay = value;
        else if (button == 1) rightDelay = value;
        else middleDelay = value;
    }
}