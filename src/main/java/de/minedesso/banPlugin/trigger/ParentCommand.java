package de.minedesso.banPlugin.trigger;

import de.minedesso.banPlugin.util.ICommand;
import de.minedesso.banPlugin.util.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentCommand implements CommandExecutor {

    private final Map<String, ICommand> subCommands = new HashMap<>();

    public ParentCommand(List<ICommand> subCommands) {
        subCommands.forEach(subCommand -> {this.subCommands.put(subCommand.name(), subCommand);});
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ICommand command = subCommands.get(label);

        if(command.playerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(MessageType.ONLY_PLAYER.message);
            return true;
        }

        if(!sender.hasPermission(command.permission())) {
            sender.sendMessage(MessageType.NO_PERMISSION.message);
            return true;
        }

        command.execute(sender, args);
        return false;
    }
}
