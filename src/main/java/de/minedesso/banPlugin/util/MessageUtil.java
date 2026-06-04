package de.minedesso.banPlugin.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    private static String convertToNoColor(String message) {
        return message.replaceAll("§[0-9a-fk-or]", "");
    }

    public static void sendMessageToUnknownSender(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            player.sendMessage(message);
        } else {
            sender.sendMessage(convertToNoColor(message));
        }
    }

}
