package de.will_smith_007.multibungeebans.sql.interfaces;

import lombok.NonNull;

import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public interface IDatabaseProvider {

    void connect();

    void closeConnection();

    void updateQuery(@NonNull String sqlQuery);

    PreparedStatement preparedStatement(@NonNull String sqlQuery);

    CompletableFuture<PreparedStatement> preparedStatementAsync(@NonNull String sqlQuery);
}
