package com.sliceclient.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import org.apache.logging.log4j.LogManager;

/**
 * Utility class for logging messages to the minecraft chat.
 *
 * @author Dylan
 */
@UtilityClass
public class LoggerUtil {

    /**
     * Logs a message to the minecraft chat.
     *
     * @param message The message to log.
     */
    public static void addMessage(String message) {

        if(MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().world == null)
            return;

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(
                "§cSlice §7» " + message.replace("&", "§")
        ));
    }

    /**
     * Logs a message to the minecraft chat.
     *
     * @param message The message to log.
     */
    public static void addMessageNoPrefix(String message) {

        if(MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().world == null)
            return;

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal(message));
    }

    /**
     * Logs a message to the terminal.
     *
     * @param message The message to log.
     */
    public static void addTerminalMessage(String message) {
        System.out.println(message);
    }

    /**
     * Adds an irc message to chat.
     *
     * @param user The username
     * @param message The message
     */
    public static void addIRCMessage(String user, String message) {
        try {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.translatable(
                    "§6IRC §7» §5" + user + "§r: " + message.replace("&", "§").replace("§k","")
            ));
        } catch(Exception ignored){}
    }
}
