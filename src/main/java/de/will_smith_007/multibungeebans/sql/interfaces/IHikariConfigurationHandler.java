package de.will_smith_007.multibungeebans.sql.interfaces;

import com.zaxxer.hikari.HikariDataSource;

public interface IHikariConfigurationHandler {

    HikariDataSource getHikariDataSource();
}
