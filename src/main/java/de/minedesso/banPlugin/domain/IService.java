package de.minedesso.banPlugin.domain;

import de.minedesso.banPlugin.util.exception.PlayerAlreadyBannedException;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public interface IService {

    void ban(CommandSender sender, String targetName, String reasonId, String duration)
            throws PlayerAlreadyBannedException, IllegalArgumentException;

    boolean isBanned(UUID uuid);
}
