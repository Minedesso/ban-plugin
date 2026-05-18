package de.minedesso.banPlugin.trigger.command.sub;

import de.minedesso.banPlugin.domain.BanService;
import de.minedesso.banPlugin.util.MessageType;
import de.minedesso.banPlugin.util.MessageUtil;
import de.minedesso.banPlugin.util.PermissionType;
import de.minedesso.banPlugin.util.SubCommand;
import org.bukkit.command.CommandSender;

public class ReasonCommand implements SubCommand {
    @Override
    public String name() {
        return "banreasons";
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
        if(args.length != 0) {
            MessageUtil.sendMessageToUnknownSender(sender, MessageType.USAGE.message + "/banreasons");
        }

        BanService.getInstance().sendBanReasons(sender);
    }
}
