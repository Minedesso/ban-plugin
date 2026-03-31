package de.minedesso.banPlugin.domain;

import de.minedesso.banPlugin.api.in.BanDetailsDto;
import de.minedesso.banPlugin.util.exception.PlayerAlreadyBannedException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface BanUseCase {

    void ban(CommandSender sender, String targetName, String reasonId, String duration)
            throws PlayerAlreadyBannedException, IllegalArgumentException;

    boolean isBanned(UUID uuid);

    void kickPlayer(Player player, BanDetailsDto banDetailsDto);
}
