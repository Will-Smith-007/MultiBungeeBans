package de.will_smith_007.multibungeebans.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariDataSource;
import de.will_smith_007.multibungeebans.sql.interfaces.IDatabaseProvider;
import de.will_smith_007.multibungeebans.sql.interfaces.IHikariConfigurationHandler;
import lombok.NonNull;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Singleton
public class DatabaseProvider implements IDatabaseProvider {

    private final Logger logger;
    private volatile Connection connection;
    private volatile HikariDataSource hikariDataSource;
    private final IHikariConfigurationHandler hikariConfigurationHandler;

    @Inject
    public DatabaseProvider(@NonNull Plugin plugin,
                            @NonNull IHikariConfigurationHandler hikariConfigurationHandler) {
        this.logger = plugin.getLogger();
        this.hikariConfigurationHandler = hikariConfigurationHandler;

        connect();
    }

    @Override
    public void connect() {
        try {
            hikariDataSource = hikariConfigurationHandler.getHikariDataSource();
            connection = hikariDataSource.getConnection();

            logger.info("Connection to the sql database was established.");

            createDefaultTables();
        } catch (SQLException | NullPointerException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        if (connection == null) return;

        hikariDataSource.close();
        logger.info("SQL database connection was closed.");
    }

    @Override
    public void updateQuery(@NonNull String sqlQuery) {
        try {
            if (connection == null || connection.isClosed()) connect();

            final Statement statement = connection.createStatement();
            statement.executeUpdate(sqlQuery);
            statement.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public PreparedStatement preparedStatement(@NonNull String sqlQuery) {
        try {
            if (connection == null || connection.isClosed()) connect();

            return connection.prepareStatement(sqlQuery);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public CompletableFuture<PreparedStatement> preparedStatementAsync(@NonNull String sqlQuery) {
        return CompletableFuture.supplyAsync(() -> preparedStatement(sqlQuery));
    }

    private void createDefaultTables() {
        updateQuery("CREATE TABLE IF NOT EXISTS multi_bungee_bans(banID BIGINT(20) AUTO_INCREMENT " +
                "PRIMARY KEY, bannedUUID VARCHAR(64) UNIQUE, bannedName VARCHAR(32), bannedBy VARCHAR(32), " +
                "reason VARCHAR(64), bannedDate VARCHAR(64), unbanDate VARCHAR(64), permanentlyBanned BOOL);");

        updateQuery("CREATE TABLE IF NOT EXISTS multi_bungee_players(uuid VARCHAR(64) PRIMARY KEY, " +
                "username VARCHAR(32));");
    }
}
