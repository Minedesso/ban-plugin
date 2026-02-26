package de.minedesso.banPlugin.domain;

import de.minedesso.banPlugin.util.exception.PlayerAlreadyBannedException;
import org.bukkit.command.CommandSender;

public interface IService {

    public void ban(CommandSender sender, String targetName, String reasonId, String duration) throws PlayerAlreadyBannedException, IllegalArgumentException;

}
