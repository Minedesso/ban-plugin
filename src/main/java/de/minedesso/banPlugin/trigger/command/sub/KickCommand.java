package de.minedesso.banPlugin.trigger.command.sub;

import de.minedesso.banPlugin.domain.BanService;
import de.minedesso.banPlugin.util.MessageType;
import de.minedesso.banPlugin.util.MessageUtil;
import de.minedesso.banPlugin.util.PermissionType;
import de.minedesso.banPlugin.util.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

public class KickCommand implements SubCommand {
    @Override
    public String name() {
        return "kick";
    }

    @Override
    public String permission() {
        return PermissionType.BAN_KICK.perm;
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            MessageUtil.sendMessageToUnknownSender(sender,
                    MessageType.USAGE + "/kick <player> <reason>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            MessageUtil.sendMessageToUnknownSender(sender,
                    MessageType.PLAYER_ALREADY_BANNED.message.replace("%player%", args[0]));
            return;
        }

        String reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        BanService.getInstance().kickPlayer(sender, target, reason);
    }
}
