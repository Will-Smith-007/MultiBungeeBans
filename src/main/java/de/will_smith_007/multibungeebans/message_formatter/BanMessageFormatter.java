package de.will_smith_007.multibungeebans.message_formatter;

import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.banned_user.BannedUser;
import lombok.NonNull;

import java.time.format.DateTimeFormatter;

/**
 * This class has only the responsibility to format a kick or ban message.
 * <br><br>The kick message format is used when a player gets kicked because he was banned.
 * It doesn't have the ban id or unban date inside.
 * <br><br>The ban message format is used when a player can't join the network because he is banned.
 * It contains all ban information including unban date and ban id.
 */
@Singleton
public class BanMessageFormatter {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Formats a kick message with the given information from the {@link BannedUser}.
     *
     * @param bannedUser Banned user which contains the ban information.
     * @return A formatted kick message as a {@link String}.
     */
    public String formatKickMessage(@NonNull BannedUser bannedUser) {
        final String durationType = (bannedUser.isPermanentlyBanned() ? "permanently" : "temporarily");

        return String.format("""
                        §8× §4§lMultiBungeeBans §8×
                                        
                        §cYou were %s banned from the network.
                                        
                        §fReason: §c%s
                        §fBanned by: §c%s
                        §fBanned on: §c%s
                                        
                        §8× §4§lMultiBungeeBans §8×
                        """,
                durationType,
                bannedUser.getBanReason(),
                bannedUser.getBannedBy(),
                dateTimeFormatter.format(bannedUser.getBannedDateTime()));
    }

    /**
     * Formats a ban message with the given information from the {@link BannedUser}.
     *
     * @param bannedUser Banned user which contains the ban information.
     * @return A formatted ban message as a {@link String}.
     */
    public String formatBanMessage(@NonNull BannedUser bannedUser) {
        final boolean isPermanentlyBanned = bannedUser.isPermanentlyBanned();
        final String durationType = (isPermanentlyBanned ? "permanently" : "temporarily");
        final String formattedUnbanDateTime = (isPermanentlyBanned ?
                "Never" : dateTimeFormatter.format(bannedUser.getUnbanDateTime()));

        return String.format("""
                        §8× §4§lMultiBungeeBans §8×
                                        
                        §cYou were %s banned from the network.
                                        
                        §fReason: §c%s
                        §fBanned by: §c%s
                        §fBanned on: §c%s
                        §fUnban on: §c%s
                        §fBanID: §c%d
                                        
                        §8× §4§lMultiBungeeBans §8×
                        """,
                durationType,
                bannedUser.getBanReason(),
                bannedUser.getBannedBy(),
                dateTimeFormatter.format(bannedUser.getBannedDateTime()),
                formattedUnbanDateTime,
                bannedUser.getBanID());
    }
}
