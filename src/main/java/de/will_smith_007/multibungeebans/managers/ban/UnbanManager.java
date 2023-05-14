package de.will_smith_007.multibungeebans.managers.ban;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.sql.interfaces.IDatabaseProvider;
import lombok.NonNull;

import java.sql.SQLException;

@Singleton
public class UnbanManager {

    private final IDatabaseProvider databaseProvider;

    @Inject
    public UnbanManager(@NonNull IDatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
    }

    public void unbanPlayerAsync(@NonNull String uuidOrName) {
        final String sqlQuery = "DELETE FROM multi_bungee_bans WHERE bannedUUID= ? OR bannedName= ?";

        databaseProvider.preparedStatementAsync(sqlQuery).thenAccept(preparedStatement -> {
            try {
                preparedStatement.setString(1, uuidOrName);
                preparedStatement.setString(2, uuidOrName);

                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }

    public void unbanPlayerAsync(long banID) {
        final String sqlQuery = "DELETE FROM multi_bungee_bans WHERE banID= ?";

        databaseProvider.preparedStatementAsync(sqlQuery).thenAccept(preparedStatement -> {
            try {
                preparedStatement.setLong(1, banID);

                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }
}
