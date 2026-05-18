package de.minedesso.banPlugin.trigger.listener;

import de.minedesso.banPlugin.domain.BanService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final BanService banService;

    public PlayerJoinListener(BanService banService) {
        this.banService = banService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (banService.isBanned(uuid)) {
            banService.kickPlayer(player);
        }
    }

}
