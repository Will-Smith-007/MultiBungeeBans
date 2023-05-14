package de.will_smith_007.multibungeebans.listeners;

import com.google.inject.Inject;
import de.will_smith_007.multibungeebans.banned_user.BannedUser;
import de.will_smith_007.multibungeebans.managers.ban.BanInformationManager;
import de.will_smith_007.multibungeebans.managers.ban.UnbanManager;
import de.will_smith_007.multibungeebans.managers.user.UserManager;
import de.will_smith_007.multibungeebans.message_formatter.BanMessageFormatter;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlayerConnectionListener implements Listener {

    private final BanInformationManager banInformationManager;
    private final UserManager userManager;
    private final UnbanManager unbanManager;
    private final BanMessageFormatter banMessageFormatter;

    @Inject
    public PlayerConnectionListener(@NonNull BanInformationManager banInformationManager,
                                    @NonNull UserManager userManager,
                                    @NonNull UnbanManager unbanManager,
                                    @NonNull BanMessageFormatter banMessageFormatter) {
        this.banInformationManager = banInformationManager;
        this.userManager = userManager;
        this.unbanManager = unbanManager;
        this.banMessageFormatter = banMessageFormatter;
    }

    @EventHandler
    public void onPlayerLogin(@NonNull LoginEvent loginEvent) {
        final PendingConnection pendingConnection = loginEvent.getConnection();
        final UUID playerUUID = pendingConnection.getUniqueId();
        final String username = pendingConnection.getName();

        userManager.updatePlayerDataAsync(playerUUID, username);

        final String uuidString = playerUUID.toString();
        final BannedUser bannedUser = banInformationManager.getBanInformation(uuidString);
        if (bannedUser == null) return;

        final LocalDateTime unbanDateTime = bannedUser.getUnbanDateTime();
        if (unbanDateTime != null) {
            final LocalDateTime currentLocalDateTime = LocalDateTime.now();

            if (currentLocalDateTime.isAfter(unbanDateTime)) {
                unbanManager.unbanPlayerAsync(uuidString);
                return;
            }
        }

        final String formattedBanMessage = banMessageFormatter.formatBanMessage(bannedUser);
        loginEvent.setCancelReason(new TextComponent(formattedBanMessage));
        loginEvent.setCancelled(true);
    }
}
