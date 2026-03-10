package io.github.penguin.fastclick;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FastClick implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger("FastClick");
    public static KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.parse("fastclick"));
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        Config.load();

        var command = literal("toggle")
                .executes(context -> {
                    toggle();
                    return 1;
                });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                dispatcher.register(literal("fastclick")
                        .then(command)
                );
                dispatcher.register(literal("fc")
                        .then(command)
                );
        });

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.fastclick.toggle", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKey.consumeClick()) {
                toggle();
            }
        });
    }

    public static void toggle() {
        Config.enabled = !Config.enabled;
        ChatUtils.setOverlayMessage(Component.literal("FastClick " + (Config.enabled ? "enabled" : "disabled")).setStyle(Style.EMPTY
                .withColor(Config.enabled ? ChatFormatting.GREEN : ChatFormatting.DARK_RED))
        );
    }
}