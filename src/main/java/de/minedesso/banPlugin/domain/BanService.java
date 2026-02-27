package de.minedesso.banPlugin.domain;

import de.minedesso.banPlugin.BanPlugin;
import de.minedesso.banPlugin.api.BanApiService;
import de.minedesso.banPlugin.api.ReasonApiService;
import de.minedesso.banPlugin.api.out.BanDto;
import de.minedesso.banPlugin.trigger.command.BanCommand;
import de.minedesso.banPlugin.trigger.command.ParentCommand;
import de.minedesso.banPlugin.util.exception.PlayerAlreadyBannedException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    @Override
    public boolean isBanned(UUID uuid) {
        return banApiService.isPlayerBanned(uuid);
    }

    @Override
    public void kickPlayer(Player player) {
        BanDto banDto = banApiService.getBan(player.getUniqueId());
        if (banDto != null) {
            String kickMessage = getKickMessage(
                    banDto.getBannedBy(),
                    banDto.getBannedAt(),
                    reasonApiService.getReasonById(banDto.getReasonId()).getDescription(),
                    banApiService.getBanExpiration(banDto)
            );
            player.kickPlayer(kickMessage);
        }
    }

    private String getKickMessage(String bannedBy, LocalDateTime bannedAt, String reason, LocalDateTime expiration) {
        String bannedAtStr = bannedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String expirationStr = getExpirationAsString(expiration);
        return String.format("""
                &c-= GOODBYE! =-
                
                &7You have been banned by &6%s
                &7Banned on: &b%s
                &7Reason: &b%s
                
                &7Expires in: &b%s
                
                &3You can appeal this ban at:
                &bhttps://minedesso.de/appeal
                """, bannedBy, bannedAtStr, reason, expirationStr);
    }

    private String getExpirationAsString(LocalDateTime expiration) {
        if (expiration == null) {
            return "PERMANENT";
        }

        // Calculate the duration between now and the expiration time
        Duration duration = Duration.between(expiration, LocalDateTime.now());
        StringBuilder expirationStr = convertDurationToString(duration);
        if (expirationStr.isEmpty()) {
            return "less than a minute";
        }
        // Remove the trailing comma and space
        return expirationStr.substring(0, expirationStr.length() - 2);
    }

    private StringBuilder convertDurationToString(Duration duration) {
        long years = duration.toDays() / 365;
        long days = duration.toDays() % 365;
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        StringBuilder expirationStr = new StringBuilder();
        if (years > 0) {
            expirationStr.append(appendTimeUnit(years, "year"));
        }
        if (days > 0) {
            expirationStr.append(appendTimeUnit(days, "day"));
        }
        if (hours > 0) {
            expirationStr.append(appendTimeUnit(hours, "hour"));
        }
        if (minutes > 0) {
            expirationStr.append(appendTimeUnit(minutes, "minute"));
        }
        return expirationStr;
    }

    private String appendTimeUnit(long value, String unit) {
        if (value > 0) {
            return value + " " + unit + (value > 1 ? "s" : "") + ", ";
        }
        return "";
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
