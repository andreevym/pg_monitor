package io.github.tbk.postrics.postgres;

import com.google.common.base.Throwables;
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

    private final String databaseName;
    private final DataSource dataSource;

    public PostgresDao(String databaseName, DataSource dataSource) {
        this.databaseName = databaseName;
        this.dataSource = requireNonNull(dataSource);
    }

    public Observable<String> getDatabaseName() {
        return getDatabaseNames()
                .filter(name -> name.equals(this.databaseName));
    }

    private Observable<String> getDatabaseNames() {
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

    public Observable<?> getTableStats(String schemaName, String tableName) {
        return Observable.fromCallable(() -> getValue("", rs -> {
            try {
                final PgTableStats.PgTableStatsBuilder builder = PgTableStats.builder()
                        .analyze_count(rs.getLong("analyze_count"))
                        .autoanalyze_count(0)
                        .autovacuum_count(0)
                        .idx_scan(0)
                        .idx_tup_fetch(0)
                        .last_analyze(0)
                        .last_autoanalyze(0)
                        .last_autovacuum(0)
                        .last_vacuum(0)
                        .n_dead_tup(0)
                        .n_live_tup(0)
                        .n_tup_hot_upd(0);

                return Optional.of(builder.build());
            } catch (SQLException e) {
                throw Throwables.propagate(e);
            }
        }))
                .map(o -> o.map(Observable::just))
                .flatMap(o -> o.orElse(Observable.empty()));
    }
}