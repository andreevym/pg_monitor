package io.github.tbk.postrics.postgres;

import io.github.tbk.postrics.postgres.command.Command;
import io.github.tbk.postrics.postgres.command.CommandImpl;
import io.github.tbk.postrics.postgres.command.Mapper;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

import java.sql.SQLException;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public final class Commands {
    @Value
    @Builder
    public static class PgVersion {
        private String full;
        private String value;
        private int numeric;
    }

    public static final BiFunction<String, String, Command<Long>> ROW_COUNT_BY_COLUMN =
            (tableName, columnName) -> Command.GET_LONG
                    .sql("select count(" + columnName + ") from " + tableName)
                    .build();

    public static final Function<String, Command<Long>> ROW_COUNT = tableName -> ROW_COUNT_BY_COLUMN
            .apply(tableName, "0");

    public static final Command<PgVersion> SHOW_VERSION = CommandImpl.<Commands.PgVersion>builder()
            .sql("SELECT version()")
            .mapper(rs -> Mapper.STRING.transform(rs)
                    .map(val -> Commands.PgVersion.builder()
                            .full(val)
                            .build()
                    )
            )
            .build();

    public static final Command<String> SELECT_DATABASES = Command.GET_STRING
            .sql("select datname from pg_database where datistemplate = false")
            .build();

    public static final Command<String> SELECT_CURRENT_DATABASE = Command.GET_STRING
            .sql("SELECT current_database()")
            .build();

    public static final Command<Long> SELECT_GLOBAL_PROCESS_COUNT = Command.GET_LONG
            .sql("select count(0) from pg_stat_activity where pid <> pg_backend_pid()")
            .build();

    public static final Command<Long> SELECT_GLOBAL_IDLE_PROCESS_COUNT = Command.GET_LONG
            .sql("select count(0) from pg_stat_activity where state LIKE 'idle%' and pid <> pg_backend_pid()")
            .build();

    public static final Command<Long> SELECT_GLOBAL_ACTIVE_PROCESS_COUNT = Command.GET_LONG
            .sql("select count(0) from pg_stat_activity where state = 'active' and pid <> pg_backend_pid()")
            .build();

    public static final Function<String, Command<Long>> SELECT_DATABASE_PROCESS_COUNT = databaseName -> Command.GET_LONG
            .sql("select count(0) from pg_stat_activity where datname='" + databaseName + "' and pid <> pg_backend_pid()")
            .build();

    public static final Command<Long> SELECT_GLOBAL_SIZE = Command.GET_LONG
            .sql("SELECT SUM(pg_database_size(oid)) FROM pg_database")
            .build();

    public static final Function<String, Command<Long>> SELECT_DATABASE_SIZE = databaseName -> Command.GET_LONG
            .sql("select pg_database_size('" + databaseName + "')")
            .build();


    public static final Function<String, Command<Long>> SELECT_FROM_PG_STAT = columnName -> Command.GET_LONG
            .sql("select sum(" + columnName + ") from pg_stat_database")
            .build();
    public static final Command<Long> SELECT_GLOBAL_COMMIT_COUNT = SELECT_FROM_PG_STAT.apply("xact_commit");
    public static final Command<Long> SELECT_GLOBAL_ROLLBACK_COUNT = SELECT_FROM_PG_STAT.apply("xact_rollback");

    public static final BiFunction<String, String, Command<Long>> SELECT_FROM_PG_STAT_FOR_DATABASE =
            (databaseName, columnName) -> Command.GET_LONG
                    .sql("select sum(" + columnName + ") from pg_stat_database  where datname = '" + databaseName + "'")
                    .build();
    public static final Function<String, Command<Long>> SELECT_DATABASE_COMMIT_COUNT = databaseName ->
            SELECT_FROM_PG_STAT_FOR_DATABASE.apply(databaseName, "xact_commit");
    public static final Function<String, Command<Long>> SELECT_DATABASE_ROLLBACK_COUNT = databaseName ->
            SELECT_FROM_PG_STAT_FOR_DATABASE.apply(databaseName, "xact_rollback");


    public static final Function<String, Command<PgTableStats>> SELECT_PG_STAT_FOR_DATABASE = databaseName -> CommandImpl.<PgTableStats>builder()
            .sql("select * from pg_stat_database where datname = '" + databaseName + "'")
            .mapper(rs -> {
                try {
                    return Observable.just(PgTableStats.builder()
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
                            .n_tup_hot_upd(0)
                            .build());
                } catch (SQLException e) {
                    return Observable.error(e);
                }
            })
            .build();
}
