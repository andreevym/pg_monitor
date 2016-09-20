package io.github.tbk.postrics.postgres.command;

import lombok.extern.slf4j.Slf4j;
import rx.AsyncEmitter;
import rx.Observable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class CommandExecutor {
    private final DataSource dataSource;

    public CommandExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> Observable<T> execute(Command<T> command) {
        return Observable.<ResultSet>fromEmitter(emitter -> {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement stmt = connection.prepareStatement(command.sql())) {
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            emitter.onNext(rs);
                        }
                    }
                }
                emitter.onCompleted();
            } catch (SQLException e) {
                log.warn("", e);
                emitter.onError(e);
            }
        }, AsyncEmitter.BackpressureMode.BUFFER)
                .flatMap(rs -> command.mapper().transform(rs));
    }
}
