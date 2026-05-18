package de.minedesso.banPlugin.domain;

import de.minedesso.banPlugin.BanPlugin;
import de.minedesso.banPlugin.api.BanApiService;
import de.minedesso.banPlugin.api.ReasonApiService;
import de.minedesso.banPlugin.api.in.Ban;
import de.minedesso.banPlugin.api.out.BanDto;
import de.minedesso.banPlugin.trigger.command.ParentCommand;
import de.minedesso.banPlugin.trigger.command.sub.BanCommand;
import de.minedesso.banPlugin.trigger.command.sub.ReasonCommand;
import de.minedesso.banPlugin.util.MessageType;
import de.minedesso.banPlugin.util.MessageUtil;
import de.minedesso.banPlugin.util.exception.PlayerAlreadyBannedException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BanService implements BanUseCase {

    private final BanApiService banApiService;
    private final ReasonApiService reasonApiService;
    private static BanService instance;
    private static final String APPEAL_URL = "https://minedesso.de/appeal";

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
        UUID playerUUID = getPlayerUUID(sender);

        BanDto banDto = new BanDto(reasonIdLong, duration, now, targetName, playerUUID);
        Ban ban = banApiService.createBan(banDto);
        if (ban != null) {
            Player target = Bukkit.getPlayer(targetName);
            if (target != null) {
                kickPlayer(target, ban);
            }
            return;
        }
        throw new IllegalArgumentException("Failed to create ban");
    }

    @Override
    public boolean isBanned(UUID uuid) {
        return banApiService.isPlayerBanned(uuid);
    }

    @Override
    public void kickPlayer(Player player, Ban ban) {
        String kickMessage = getKickMessage(
                ban.getBannedBy(),
                ban.getBannedAt(),
                ban.getReason(),
                ban.getExpiresAt()
        );
        player.kickPlayer(kickMessage);
    }

    public void kickPlayer(Player player) {
        Ban ban = banApiService.getBan(player.getUniqueId());
        if(ban != null) kickPlayer(player, ban);
    }

    public void sendBanReasons(CommandSender sender) {
        String border = MessageType.PREFIX.message + "&cBan reasons:\n";
        List<String> reasons = reasonApiService.getReasons().stream()
                .map(reason -> String.format("&l&9%d&r &7- &b%s\n", reason.getReasonId(), reason.getReason()))
                .toList();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(border);
        reasons.forEach(stringBuilder::append);
        MessageUtil.sendMessageToUnknownSender(sender, stringBuilder.append(border).toString());
    }

    private String getKickMessage(String bannedBy, LocalDateTime bannedAt, String reason, LocalDateTime expiration) {
        String bannedAtStr = bannedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String expirationStr = getExpirationAsString(expiration);
        if (bannedBy == null) bannedBy = "SYSTEM";
        return String.format("""
                &c-= GOODBYE! =-
                
                &7You have been banned by &6%s
                &7Banned on: &b%s
                &7Reason: &b%s
                
                &7Expires in: &b%s
                
                &3You can appeal this ban at:
                &b%s
                """, bannedBy, bannedAtStr, reason, expirationStr, APPEAL_URL);
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

    private UUID getPlayerUUID(CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }
        return null; // For console or other non-player senders, we can return null or a specific UUID
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
                new BanCommand(),
                new ReasonCommand()
                // Future commands like unban, kick etc. can be added here
        ));

        Objects.requireNonNull(BanPlugin.getInstance().getCommand("/ban")).setExecutor(parentCommand);
    }
}
