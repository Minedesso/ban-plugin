package de.minedesso.banPlugin.domain;

import de.minedesso.banPlugin.api.in.Ban;
import de.minedesso.banPlugin.util.exception.PlayerAlreadyBannedException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface BanUseCase {

    void ban(CommandSender sender, String targetName, String reasonId, String duration)
            throws PlayerAlreadyBannedException, IllegalArgumentException;

    boolean isBanned(UUID uuid);

    void kickPlayerAfterBan(Player player, Ban ban);

    void kickPlayer(CommandSender sender, Player target, String reason);
}
