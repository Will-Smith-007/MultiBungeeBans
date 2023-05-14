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
import java.util.List;
import java.util.UUID;

public class BanCommand extends Command implements TabExecutor {

    private final BanManager banManager;
    private final UserManager userManager;
    private final IRedisSubscribePublishHandler redisSubscribePublishHandler;
    private final BanMessageFormatter banMessageFormatter;

    @Inject
    public BanCommand(@NonNull @Named("BanCommand") String name,
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
        if (!sender.hasPermission("multibans.ban")) {
            sender.sendMessage(new TextComponent(Message.NO_PERMISSION.getMessage()));
            return;
        }

        final int length = args.length;

        if (length > 1) {
            final String targetUsername = args[0];
            final StringBuilder stringReasonBuilder = new StringBuilder();

            for (int index = 1; index != length; index++) {
                stringReasonBuilder.append(args[index]).append(" ");
            }
            stringReasonBuilder.deleteCharAt((stringReasonBuilder.length() - 1));

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
                    .bannedDateTime(LocalDateTime.now())
                    .isPermanentlyBanned(true)
                    .build();

            banManager.banPlayerAsync(bannedUser);

            sender.sendMessage(new TextComponent(Message.PREFIX + "§aYou have §4permanently§a banned §e" +
                    targetUsername + "§a with the reason §e" + stringReasonBuilder + "§a."));

            final String formattedKickMessage = banMessageFormatter.formatKickMessage(bannedUser);
            final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerUUID);

            if (proxiedPlayer == null) {
                redisSubscribePublishHandler.publish("BanChannel", "ban#" +
                        playerUUID + "#" + formattedKickMessage);
                return;
            }

            proxiedPlayer.disconnect(new TextComponent(formattedKickMessage));
        } else {
            sender.sendMessage(new TextComponent(Message.PREFIX + "§cPlease use the following command: " +
                    "§e/ban [Player] [BanReason]"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(@NonNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("multibans.ban")) return null;

        final int length = args.length;
        if (length == 1) {
            return ProxyServer.getInstance().getPlayers().stream()
                    .map(ProxiedPlayer::getName)
                    .toList();
        } else if (length > 1) {
            return List.of("BanReason");
        }
        return null;
    }
}
