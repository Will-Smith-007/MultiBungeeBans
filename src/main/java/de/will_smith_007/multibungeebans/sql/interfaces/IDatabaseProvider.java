package de.will_smith_007.multibungeebans.sql.interfaces;

import lombok.NonNull;

import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public interface IDatabaseProvider {

    /**
     * Executes a sql update query. Should only be used to create tables.
     *
     * @param sqlQuery The sql query which should be executed by the database.
     */
    void updateQuery(@NonNull String sqlQuery);

    /**
     * Performs a {@link PreparedStatement} with the given sql query.
     *
     * @param sqlQuery The sql query which should be performed by the database.
     * @return A {@link PreparedStatement} which can execute an update or can return a {@link java.sql.ResultSet}.
     */
    PreparedStatement preparedStatement(@NonNull String sqlQuery);

    /**
     * Performs a {@link PreparedStatement} with the given sql query asynchronously.
     * @param sqlQuery The sql query which should be performed by the database.
     * @return A {@link CompletableFuture} which contains the {@link PreparedStatement}
     * which can execute an update or can return a {@link java.sql.ResultSet}.
     */
    CompletableFuture<PreparedStatement> preparedStatementAsync(@NonNull String sqlQuery);
}
