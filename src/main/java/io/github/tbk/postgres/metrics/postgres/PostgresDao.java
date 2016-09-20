package io.github.tbk.postgres.metrics.postgres;

import lombok.extern.slf4j.Slf4j;
import rx.AsyncEmitter;
import rx.Observable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Slf4j
public class PostgresDao {

    private Function<ResultSet, Optional<Integer>> intFn = res -> {
        try {
            return Optional.of(res.getInt(1));
        } catch (SQLException e) {
            log.warn("", e);
            return Optional.empty();
        }
    };

    private Function<ResultSet, Optional<Double>> doubleFn = res -> {
        try {
            return Optional.of(res.getDouble(1));
        } catch (SQLException e) {
            log.warn("", e);
            return Optional.empty();
        }
    };

    private DataSource dataSource;

    public PostgresDao(DataSource dataSource) {
        this.dataSource = requireNonNull(dataSource);
    }

    public Observable<String> getDatabaseNames() {
        return Observable.fromEmitter(emitter -> {
            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement stmt = conn.prepareStatement("select datname from pg_database where datistemplate=?")) {
                    stmt.setBoolean(1, false);
                    try (ResultSet res = stmt.executeQuery()) {
                        while (res.next()) {
                            emitter.onNext(res.getString("datname"));
                        }
                        emitter.onCompleted();
                    }
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        }, AsyncEmitter.BackpressureMode.BUFFER);
    }

    public <T> Optional<T> getValue(String sql, Function<ResultSet, Optional<T>> transformer) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet res = stmt.executeQuery()) {
                    if (res.next()) {
                        return Optional.ofNullable(transformer.apply(res))
                                .orElse(Optional.empty());
                    }
                }
            }
        } catch (SQLException e) {
            log.warn("", e);
        }

        return Optional.empty();
    }

    public Optional<Integer> getInt(String sql) {
        return getValue(sql, intFn);
    }

    public Optional<Double> getDouble(String sql) {
        return getValue(sql, doubleFn);
    }

    public Observable<PgTableStats> getTableStats(String schemaName, String tableName) {
        return Observable.empty();
    }
}