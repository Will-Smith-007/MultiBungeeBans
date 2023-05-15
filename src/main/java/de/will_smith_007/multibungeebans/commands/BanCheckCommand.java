package de.will_smith_007.multibungeebans.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.will_smith_007.multibungeebans.banned_user.BannedUser;
import de.will_smith_007.multibungeebans.enums.Message;
import de.will_smith_007.multibungeebans.managers.ban.BanInformationManager;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * This {@link Command} allows you to check the ban information of a player.
 */
public class BanCheckCommand extends Command implements TabExecutor {

    private final BanInformationManager banInformationManager;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Inject
    public BanCheckCommand(@NonNull @Named("BanCheckCommand") String name,
                           @NonNull BanInformationManager banInformationManager) {
        super(name);
        this.banInformationManager = banInformationManager;
    }

    @Override
    public void execute(@NonNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("multibans.bancheck")) {
            sender.sendMessage(new TextComponent(Message.NO_PERMISSION.getMessage()));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(new TextComponent(Message.PREFIX + "§cPlease use the following command: " +
                    "§e/bancheck [Player/UUID/BanID]"));
            return;
        }

        final String targetUsernameOrUUID = args[0];

        // If it can parse a long, then get the information from the banID. Otherwise, from the name or uuid
        try {
            final long banID = Long.parseLong(targetUsernameOrUUID);
            // Gets information from the banID
            banInformationManager.getBanInformationAsync(banID).thenAccept(bannedUser -> {
                if (bannedUser == null) {
                    sender.sendMessage(new TextComponent(Message.PREFIX + "§cCouldn't find any bans for ID §e" + banID + "§c."));
                    return;
                }
                sendBanInformationMessage(sender, bannedUser);
            });
        } catch (NumberFormatException numberFormatException) {
            // Gets information from the username or uuid
            banInformationManager.getBanInformationAsync(targetUsernameOrUUID).thenAccept(bannedUser -> {
                if (bannedUser == null) {
                    sender.sendMessage(new TextComponent(Message.PREFIX + "§cCouldn't find any bans for player or uuid §e" +
                            targetUsernameOrUUID + "§c."));
                    return;
                }
                sendBanInformationMessage(sender, bannedUser);
            });
        }
    }

    @Override
    public Iterable<String> onTabComplete(@NonNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("multibans.bancheck")) return null;
        if (args.length == 1) {
            return Arrays.asList("Username", "UUID", "BanID");
        }
        return null;
    }

    /**
     * Sends the standard ban information message to the player who requested the information.
     *
     * @param sender     Sender which requested the information and to which the message should be sent.
     * @param bannedUser Banned user object to get the data from it.
     */
    private void sendBanInformationMessage(@NonNull CommandSender sender, @NonNull BannedUser bannedUser) {
        final boolean isPermanentlyBanned = bannedUser.isPermanentlyBanned();
        sender.sendMessage(new TextComponent(Message.PREFIX + "§c" + bannedUser.getBannedUsername() +
                "§7 is currently §e" + (isPermanentlyBanned ? "§4permanently" : "temporary") + "§7 banned."));
        sender.sendMessage(new TextComponent(Message.PREFIX + "§fReason: §c" + bannedUser.getBanReason()));
        sender.sendMessage(new TextComponent(Message.PREFIX + "§fBanned by: §c" + bannedUser.getBannedBy()));
        sender.sendMessage(new TextComponent(Message.PREFIX + "§fBanned on: §c" + dateTimeFormatter.format(bannedUser.getBannedDateTime())));

        if (!isPermanentlyBanned) {
            sender.sendMessage(new TextComponent(Message.PREFIX + "§fUnban on: §c" + dateTimeFormatter.format(bannedUser.getUnbanDateTime())));
        }

        sender.sendMessage(new TextComponent(Message.PREFIX + "§fBanID: §c" + bannedUser.getBanID()));
    }
}
