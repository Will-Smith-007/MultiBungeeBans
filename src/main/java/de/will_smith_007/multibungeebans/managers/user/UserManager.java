package de.will_smith_007.multibungeebans.managers.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.sql.interfaces.IDatabaseProvider;
import lombok.NonNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Singleton
public class UserManager {

    private final IDatabaseProvider databaseProvider;

    @Inject
    public UserManager(@NonNull IDatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    public void updatePlayerDataAsync(@NonNull UUID playerUUID, @NonNull String username) {
        final String sqlQuery = "INSERT INTO multi_bungee_players(uuid, username) VALUES (?, ?) ON DUPLICATE KEY " +
                "UPDATE username= ?";

        databaseProvider.preparedStatementAsync(sqlQuery).thenAccept(preparedStatement -> {
            try {
                preparedStatement.setString(1, playerUUID.toString());
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, username);

                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }

    public UUID getPlayerUUID(@NonNull String username) {
        final String sqlQuery = "SELECT uuid FROM multi_bungee_players WHERE username= ?";
        final PreparedStatement preparedStatement = databaseProvider.preparedStatement(sqlQuery);

        try {
            preparedStatement.setString(1, username);

            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final String stringUUID = resultSet.getString("uuid");
                return (stringUUID != null ? UUID.fromString(stringUUID) : null);
            }
            preparedStatement.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
}
