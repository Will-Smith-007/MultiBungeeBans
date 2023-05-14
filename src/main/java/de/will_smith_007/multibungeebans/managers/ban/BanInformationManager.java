package de.will_smith_007.multibungeebans.managers.ban;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.banned_user.BannedUser;
import de.will_smith_007.multibungeebans.sql.interfaces.IDatabaseProvider;
import lombok.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Singleton
public class BanInformationManager {

    private final IDatabaseProvider databaseProvider;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Inject
    public BanInformationManager(@NonNull IDatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    public @NonNull CompletableFuture<List<String>> getBannedPlayerNamesAsync() {
        final List<String> bannedPlayers = new LinkedList<>();
        final String sqlQuery = "SELECT bannedName FROM multi_bungee_bans ORDER BY bannedName ASC";

        return databaseProvider.preparedStatementAsync(sqlQuery).thenApply(preparedStatement -> {
            try {
                final ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    bannedPlayers.add(resultSet.getString("bannedName"));
                }

                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            return bannedPlayers;
        });
    }

    public BannedUser getBanInformation(@NonNull String uuidOrName) {
        final String sqlQuery = "SELECT * FROM multi_bungee_bans WHERE bannedUUID= ? OR bannedName= ?";
        final PreparedStatement preparedStatement = databaseProvider.preparedStatement(sqlQuery);

        try {
            preparedStatement.setString(1, uuidOrName);
            preparedStatement.setString(2, uuidOrName);

            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final String formattedBanDateTime = resultSet.getString("bannedDate");
                final String formattedUnbanDateTime = resultSet.getString("unbanDate");

                final LocalDateTime banDateTime = LocalDateTime.parse(formattedBanDateTime, dateTimeFormatter);
                final LocalDateTime unbanDateTime = (formattedUnbanDateTime == null ?
                        null : LocalDateTime.parse(formattedUnbanDateTime, dateTimeFormatter));

                return BannedUser.builder()
                        .banID(resultSet.getLong("banID"))
                        .bannedUUID(UUID.fromString(resultSet.getString("bannedUUID")))
                        .bannedUsername(resultSet.getString("bannedName"))
                        .bannedBy(resultSet.getString("bannedBy"))
                        .banReason(resultSet.getString("reason"))
                        .bannedDateTime(banDateTime)
                        .unbanDateTime(unbanDateTime)
                        .isPermanentlyBanned(resultSet.getBoolean("permanentlyBanned"))
                        .build();
            }

            preparedStatement.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public CompletableFuture<BannedUser> getBanInformationAsync(@NonNull String uuidOrName) {
        return CompletableFuture.supplyAsync(() -> getBanInformation(uuidOrName));
    }

    public CompletableFuture<BannedUser> getBanInformationAsync(long banID) {
        final String sqlQuery = "SELECT * FROM multi_bungee_bans WHERE banID= ?";

        return databaseProvider.preparedStatementAsync(sqlQuery).thenApply(preparedStatement -> {
            try {
                preparedStatement.setLong(1, banID);

                final ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    final String formattedBanDateTime = resultSet.getString("bannedDate");
                    final String formattedUnbanDateTime = resultSet.getString("unbanDate");

                    final LocalDateTime banDateTime = LocalDateTime.parse(formattedBanDateTime, dateTimeFormatter);
                    final LocalDateTime unbanDateTime = (formattedUnbanDateTime == null ?
                            null : LocalDateTime.parse(formattedUnbanDateTime, dateTimeFormatter));

                    return BannedUser.builder()
                            .banID(banID)
                            .bannedUUID(UUID.fromString(resultSet.getString("bannedUUID")))
                            .bannedUsername(resultSet.getString("bannedName"))
                            .bannedBy(resultSet.getString("bannedBy"))
                            .banReason(resultSet.getString("reason"))
                            .bannedDateTime(banDateTime)
                            .unbanDateTime(unbanDateTime)
                            .isPermanentlyBanned(resultSet.getBoolean("permanentlyBanned"))
                            .build();
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            return null;
        });
    }
}
