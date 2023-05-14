package de.will_smith_007.multibungeebans.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.will_smith_007.multibungeebans.enums.Message;
import de.will_smith_007.multibungeebans.managers.ban.BanInformationManager;
import de.will_smith_007.multibungeebans.managers.ban.UnbanManager;
import lombok.NonNull;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Arrays;

public class UnbanCommand extends Command implements TabExecutor {

    private final UnbanManager unbanManager;
    private final BanInformationManager banInformationManager;

    @Inject
    public UnbanCommand(@NonNull @Named("UnbanCommand") String name,
                        @NonNull UnbanManager unbanManager,
                        @NonNull BanInformationManager banInformationManager) {
        super(name);
        this.unbanManager = unbanManager;
        this.banInformationManager = banInformationManager;
    }

    @Override
    public void execute(@NonNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("multibans.unban")) {
            sender.sendMessage(new TextComponent(Message.NO_PERMISSION.getMessage()));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(new TextComponent(Message.PREFIX + "§cPlease use the following command: " +
                    "§e/unban [BanID/Username/UUID]"));
            return;
        }

        final String targetUsernameOrUUID = args[0];

        try {
            final long banID = Long.parseLong(targetUsernameOrUUID);
            banInformationManager.getBanInformationAsync(banID).thenAccept(bannedUser -> {
                if (bannedUser == null) {
                    sender.sendMessage(new TextComponent(Message.PREFIX + "§cCouldn't find any bans for the ID §e" +
                            banID + "§c."));
                    return;
                }

                unbanManager.unbanPlayerAsync(banID);
                sender.sendMessage(new TextComponent(Message.PREFIX + "§aYou have unbanned the player §e" +
                        bannedUser.getBannedUsername() + "§a."));
            });
        } catch (NumberFormatException numberFormatException) {
            banInformationManager.getBanInformationAsync(targetUsernameOrUUID).thenAccept(bannedUser -> {
                if (bannedUser == null) {
                    sender.sendMessage(new TextComponent(Message.PREFIX + "§cCouldn't find any bans for UUID or " +
                            "player §e" + targetUsernameOrUUID + "§c."));
                    return;
                }

                unbanManager.unbanPlayerAsync(targetUsernameOrUUID);
                sender.sendMessage(new TextComponent(Message.PREFIX + "§aYou have unbanned the player §e" +
                        bannedUser.getBannedUsername() + "§a."));
            });
        }
    }

    @Override
    public Iterable<String> onTabComplete(@NonNull CommandSender sender, String[] args) {
        if (!sender.hasPermission("multibans.unban")) return null;
        if (args.length == 1) {
            return Arrays.asList("UUID", "Username", "BanID");
        }
        return null;
    }
}
