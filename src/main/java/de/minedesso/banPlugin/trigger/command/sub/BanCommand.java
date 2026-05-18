package de.minedesso.banPlugin.trigger.command.sub;

import de.minedesso.banPlugin.domain.BanService;
import de.minedesso.banPlugin.util.SubCommand;
import de.minedesso.banPlugin.util.MessageType;
import de.minedesso.banPlugin.util.MessageUtil;
import de.minedesso.banPlugin.util.PermissionType;
import de.minedesso.banPlugin.util.exception.PlayerAlreadyBannedException;
import org.bukkit.command.CommandSender;

public class BanCommand implements SubCommand {

    @Override
    public String name() {
        return "ban";
    }

    @Override
    public String permission() {
        return PermissionType.BAN_USE.perm;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 3) {
            sender.sendMessage(MessageUtil.convertToNoColor(
                    MessageType.USAGE.message + "/ban <player|uuid> <reasonId> <duration>"));
            sender.sendMessage(MessageUtil.convertToNoColor(
                    MessageType.USAGE.message + "Duration format examples: '1d', '2h', '30m', '2y', 'perm'"));
            return;
        }

        String targetName = args[0];
        String reasonId = args[1];
        String duration = args[2];

        try {
            BanService.getInstance().ban(sender, targetName, reasonId, duration);
        } catch (PlayerAlreadyBannedException e) {
            MessageUtil.sendMessageToUnknownSender(
                    sender,
                    MessageType.PLAYER_ALREADY_BANNED.message.replace("%player%", targetName));
        } catch (IllegalArgumentException e) {
            MessageUtil.sendMessageToUnknownSender(sender, MessageType.PREFIX.message + e.getMessage());
        }
    }
}
