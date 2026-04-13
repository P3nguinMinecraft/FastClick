package io.github.penguin.fastclick;

import com.mojang.blaze3d.platform.InputConstants;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
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

        var toggle = literal("toggle")
                .executes(context -> {
                    toggle();
                    return 1;
                });
        var reset = literal("reset")
                .executes(context -> {
                    Config.setDefault();
                    Config.save();
                    ChatUtils.setOverlayMessage(Component.literal("Config reset to default").setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
                    return 1;
                });

        var delay = literal("delay")
                .then(argument("delay_ticks", IntegerArgumentType.integer(0, 20))
                        .executes(context -> {
                            int value = IntegerArgumentType.getInteger(context, "delay_ticks");
                            Config.delay = value;
                            Config.save();
                            ChatUtils.setOverlayMessage(Component.literal("Delay set to " + value).setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)));
                            return 1;
                        })
                );
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                dispatcher.register(literal("fastclick")
                        .then(toggle)
                        .then(reset)
                        .then(delay)
                );
                dispatcher.register(literal("fc")
                        .then(toggle)
                        .then(reset)
                        .then(delay)
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