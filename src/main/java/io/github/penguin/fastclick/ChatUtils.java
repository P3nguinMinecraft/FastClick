package io.github.penguin.fastclick;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ChatUtils {
//    public static void sendChatMessage(String message) {
//        Minecraft client = Minecraft.getInstance();
//        client.execute(() -> client.gui.getChat().addMessage(Component.literal(message)));
//    }
//
//    public static void setOverlayMessage(String message) {
//        setOverlayMessage(Component.literal(message));
//    }

    public static void setOverlayMessage(Component component) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.gui.setOverlayMessage(component, true));
    }
}
