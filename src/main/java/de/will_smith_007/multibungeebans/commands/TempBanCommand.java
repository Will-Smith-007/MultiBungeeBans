package de.will_smith_007.multibungeebans.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.will_smith_007.multibungeebans.banned_user.BannedUser;
import de.will_smith_007.multibungeebans.enums.Message;
import de.will_smith_007.multibungeebans.managers.ban.BanManager;
import de.will_smith_007.multibungeebans.managers.user.UserManager;
import de.will_smith_007.multibungeebans.message_formatter.BanMessageFormatter;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisSubscribePublishHandler;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TempBanCommand extends Command implements TabExecutor {

    private final BanManager banManager;
    private final UserManager userManager;
    private final IRedisSubscribePublishHandler redisSubscribePublishHandler;
    private final BanMessageFormatter banMessageFormatter;

    @Inject
    public TempBanCommand(@Named("TempBanCommand") String name,
                          @NonNull BanManager banManager,
                          @NonNull UserManager userManager,
                          @NonNull IRedisSubscribePublishHandler redisSubscribePublishHandler,
                          @NonNull BanMessageFormatter banMessageFormatter) {
        super(name);
        this.banManager = banManager;
        this.userManager = userManager;
        this.redisSubscribePublishHandler = redisSubscribePublishHandler;
        this.banMessageFormatter = banMessageFormatter;
    }

    @Override
    public void execute(@NonNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("multibans.tempban")) {
            sender.sendMessage(new TextComponent(Message.NO_PERMISSION.getMessage()));
            return;
        }

        final int length = args.length;

        if (length < 3) {
            sender.sendMessage(new TextComponent(Message.PREFIX + "§cPlease use the following command: " +
                    "§e/tempban [Player] [TimeFormat] [BanReason]"));
            return;
        }

        final String targetUsername = args[0];
        final String timeFormat = args[1].substring(args[1].length() - 1);

        try {
            final long timeNumber = Long.parseLong(args[1].substring(0, (args[1].length() - 1)));
            final LocalDateTime localDateTime = LocalDateTime.now();
            final StringBuilder stringReasonBuilder = new StringBuilder();

            for (int index = 2; index != length; index++) {
                stringReasonBuilder.append(args[index]).append(" ");
            }
            stringReasonBuilder.deleteCharAt((stringReasonBuilder.length() - 1));

            final LocalDateTime unbanDateTime;
            switch (timeFormat) {
                case "m" -> unbanDateTime = localDateTime.plusMinutes(timeNumber);
                case "h" -> unbanDateTime = localDateTime.plusHours(timeNumber);
                case "d" -> unbanDateTime = localDateTime.plusDays(timeNumber);
                case "M" -> unbanDateTime = localDateTime.plusMonths(timeNumber);
                case "y" -> unbanDateTime = localDateTime.plusYears(timeNumber);
                default -> {
                    sender.sendMessage(new TextComponent(Message.PREFIX + "§cThe following§e time formats§c are " +
                            "currently available:§e m (Minutes), h (Hours), d (Days), M (Months), y (Years)"));
                    return;
                }
            }

            final UUID playerUUID = userManager.getPlayerUUID(targetUsername);
            if (playerUUID == null) {
                sender.sendMessage(new TextComponent(Message.PREFIX + "§cCouldn't find player §e" + targetUsername + "§c."));
                return;
            }

            final BannedUser bannedUser = BannedUser.builder()
                    .bannedUUID(playerUUID)
                    .bannedUsername(targetUsername)
                    .bannedBy(sender.getName())
                    .banReason(stringReasonBuilder.toString())
                    .bannedDateTime(localDateTime)
                    .unbanDateTime(unbanDateTime)
                    .isPermanentlyBanned(false)
                    .build();

            banManager.banPlayerAsync(bannedUser);

            sender.sendMessage(new TextComponent(Message.PREFIX + "§aYou have§e temporary§a banned §e" + targetUsername +
                    "§a with the reason §e" + stringReasonBuilder + "§a."));

            final String formattedKickMessage = banMessageFormatter.formatKickMessage(bannedUser);
            final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerUUID);

            if (proxiedPlayer == null) {
                redisSubscribePublishHandler.publish("BanChannel", "ban#" +
                        playerUUID + "#" + formattedKickMessage);
                return;
            }

            proxiedPlayer.disconnect(new TextComponent(formattedKickMessage));
        } catch (NumberFormatException numberFormatException) {
            sender.sendMessage(new TextComponent(Message.PREFIX + "§cPlease provide a valid number."));
        }
    }

    @Override
    public Iterable<String> onTabComplete(@NonNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("multibans.tempban")) return null;

        final int length = args.length;
        if (length == 1) {
            return ProxyServer.getInstance().getPlayers().stream()
                    .map(ProxiedPlayer::getName)
                    .toList();
        } else if (length == 2) {
            return Arrays.asList("1m", "1h", "1d", "1M", "1y");
        } else if (length > 2) {
            return List.of("BanReason");
        }
        return null;
    }
}
