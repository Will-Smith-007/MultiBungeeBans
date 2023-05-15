package de.will_smith_007.multibungeebans.sql;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.will_smith_007.multibungeebans.file_config.interfaces.ISQLDatabaseConfig;
import de.will_smith_007.multibungeebans.sql.interfaces.IHikariConfigurationHandler;
import lombok.NonNull;

/**
 * This class has the responsibility to set up the {@link HikariConfig} and create the {@link HikariDataSource}
 * from this configuration.
 */
@Singleton
public class HikariConfigurationHandler implements IHikariConfigurationHandler {

    private final ISQLDatabaseConfig databaseConfig;

    @Inject
    public HikariConfigurationHandler(@NonNull ISQLDatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    @Override
    public HikariDataSource getHikariDataSource() {
        final HikariConfig hikariConfig = new HikariConfig();

        final String databaseName = databaseConfig.getSQLDatabaseName();

        hikariConfig.setJdbcUrl("jdbc:mysql://" +
                databaseConfig.getSQLHost() + ":" +
                databaseConfig.getSQLPort() + "/" +
                databaseName);

        hikariConfig.setUsername(databaseConfig.getSQLUsername());
        hikariConfig.setPassword(databaseConfig.getSQLSecret());

        hikariConfig.setConnectionTestQuery("SELECT 1");

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        hikariConfig.addDataSourceProperty("maxIdleTime", 28800);

        hikariConfig.setPoolName(databaseName);
        hikariConfig.setMaximumPoolSize(2);
        hikariConfig.setMinimumIdle(5);

        return new HikariDataSource(hikariConfig);
    }
}
