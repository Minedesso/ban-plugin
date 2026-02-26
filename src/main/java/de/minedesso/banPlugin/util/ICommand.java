package de.minedesso.banPlugin.util;

import org.bukkit.command.CommandSender;

public interface ICommand {
    String name();

    String permission();

    boolean playerOnly();

    void execute(CommandSender sender, String[] args);
}
