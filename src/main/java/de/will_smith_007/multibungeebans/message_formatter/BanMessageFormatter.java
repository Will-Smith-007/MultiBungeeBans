package de.will_smith_007.multibungeebans.message_formatter;

import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.banned_user.BannedUser;
import lombok.NonNull;

import java.time.format.DateTimeFormatter;

@Singleton
public class BanMessageFormatter {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public String formatKickMessage(@NonNull BannedUser bannedUser) {
        final String durationType = (bannedUser.isPermanentlyBanned() ? "permanently" : "temporary");

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

    public String formatBanMessage(@NonNull BannedUser bannedUser) {
        final boolean isPermanentlyBanned = bannedUser.isPermanentlyBanned();
        final String durationType = (isPermanentlyBanned ? "permanently" : "temporary");
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
