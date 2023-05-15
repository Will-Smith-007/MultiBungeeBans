package de.will_smith_007.multibungeebans.managers.ban;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.banned_user.BannedUser;
import de.will_smith_007.multibungeebans.sql.interfaces.IDatabaseProvider;
import lombok.NonNull;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This manager has only the responsibility to save player bans to the sql database.
 */
@Singleton
public class BanManager {

    private final IDatabaseProvider databaseProvider;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Inject
    public BanManager(@NonNull IDatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }


    /**
     * Saves a player ban to the database with the given information in the {@link BannedUser} object asynchronously.
     *
     * @param bannedUser The banned user which contains all information of the ban.
     */
    public void banPlayerAsync(@NonNull BannedUser bannedUser) {
        final LocalDateTime unbanDateTime = bannedUser.getUnbanDateTime();
        final String formattedUnbanDateTime = (unbanDateTime == null ? null : dateTimeFormatter.format(unbanDateTime));
        final String formattedBanDateTime = dateTimeFormatter.format(LocalDateTime.now());
        final String sqlQuery = "INSERT INTO multi_bungee_bans(bannedUUID, bannedName, bannedBy, " +
                "reason, bannedDate, unbanDate, permanentlyBanned) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY " +
                "UPDATE bannedName= ?, bannedBy= ?, reason= ?, bannedDate= ?, unbanDate= ?, permanentlyBanned= ?;";

        databaseProvider.preparedStatementAsync(sqlQuery).thenAccept(preparedStatement -> {
            final String bannedUsername = bannedUser.getBannedUsername();
            final String bannedBy = bannedUser.getBannedBy();
            final String banReason = bannedUser.getBanReason();
            final boolean isPermanentlyBanned = bannedUser.isPermanentlyBanned();

            try {
                preparedStatement.setString(1, bannedUser.getBannedUUID().toString());
                preparedStatement.setString(2, bannedUsername);
                preparedStatement.setString(3, bannedBy);
                preparedStatement.setString(4, banReason);
                preparedStatement.setString(5, formattedBanDateTime);
                preparedStatement.setString(6, formattedUnbanDateTime);
                preparedStatement.setBoolean(7, isPermanentlyBanned);

                preparedStatement.setString(8, bannedUsername);
                preparedStatement.setString(9, bannedBy);
                preparedStatement.setString(10, banReason);
                preparedStatement.setString(11, formattedBanDateTime);
                preparedStatement.setString(12, formattedUnbanDateTime);
                preparedStatement.setBoolean(13, isPermanentlyBanned);

                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }
}
