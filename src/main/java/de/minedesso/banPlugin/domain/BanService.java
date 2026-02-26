package de.minedesso.banPlugin.domain;

import de.minedesso.banPlugin.BanPlugin;
import de.minedesso.banPlugin.api.BanApiService;
import de.minedesso.banPlugin.api.ReasonApiService;
import de.minedesso.banPlugin.api.out.BanDto;
import de.minedesso.banPlugin.trigger.ParentCommand;
import de.minedesso.banPlugin.trigger.sub.BanCommand;
import de.minedesso.banPlugin.util.exception.PlayerAlreadyBannedException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class BanService implements IService {

    private final BanApiService banApiService;
    private final ReasonApiService reasonApiService;
    private static BanService instance;

    private BanService() {
        banApiService = BanApiService.getInstance();
        reasonApiService = ReasonApiService.getInstance();

        initializeCommands();
    }

    public static BanService getInstance() {
        if (instance == null) {
            instance = new BanService();
        }
        return instance;
    }


    @Override
    public void ban(CommandSender sender, String targetName, String reasonId, String duration)
            throws PlayerAlreadyBannedException, IllegalArgumentException {
        if (banApiService.isPlayerBanned(targetName)) {
            throw new PlayerAlreadyBannedException(targetName);
        }

        if (!isDurationValid(duration)) {
            throw new IllegalArgumentException("Invalid duration format. Use formats like '1d', '2h', '30m', '2y'.");
        }

        if (!isReasonValid(reasonId)) {
            throw new IllegalArgumentException("Invalid reasonId");
        }

        long reasonIdLong = Long.parseLong(reasonId);
        LocalDateTime now = LocalDateTime.now();
        String playerName = getPlayerName(sender);

        BanDto banDto = new BanDto(reasonIdLong, duration, now, playerName);
        boolean success = banApiService.createBan(banDto);
        if (!success) {
            throw new IllegalArgumentException("Failed to create ban");
        }
    }

    private String getPlayerName(CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getName();
        }
        return "SYSTEM";
    }

    private boolean isReasonValid(String reasonIdString) {
        long reasonId;
        try {
            reasonId = Long.parseLong(reasonIdString);
        } catch (NumberFormatException e) {
            return false;
        }
        return reasonApiService.isReasonValid(reasonId);
    }

    /**
     * Validates the duration string to ensure it follows the expected format (e.g., "1d", "2h", "30m", "2y").
     * @param duration the duration string to validate
     * @return true if the duration is valid, false otherwise
     */
    private boolean isDurationValid(String duration) {
        return duration.matches("\\d+[smhdwy]");
    }

    private void initializeCommands() {
        ParentCommand parentCommand = new ParentCommand(List.of(
                new BanCommand()
        ));

        Objects.requireNonNull(BanPlugin.getInstance().getCommand("/ban")).setExecutor(parentCommand);
    }
}
