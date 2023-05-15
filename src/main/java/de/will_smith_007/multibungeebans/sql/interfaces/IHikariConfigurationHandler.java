package de.will_smith_007.multibungeebans.sql.interfaces;

import com.zaxxer.hikari.HikariDataSource;

public interface IHikariConfigurationHandler {

    /**
     * Used to set up the {@link com.zaxxer.hikari.HikariConfig} and create a {@link HikariDataSource} from
     * the configuration.
     *
     * @return The {@link HikariDataSource} which is based on the {@link com.zaxxer.hikari.HikariConfig}.
     */
    HikariDataSource getHikariDataSource();
}
