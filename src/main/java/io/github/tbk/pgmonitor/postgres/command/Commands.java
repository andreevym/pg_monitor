package io.github.tbk.pgmonitor.postgres.command;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Commands {
    @Value
    @Builder
    public static class PgVersion {
        private long numeric;
    }

    public static final Command<PgVersion> SHOW_VERSION = CommandImpl.<Commands.PgVersion>builder()
            .sql("show server_version_num")
            .mapper(rs -> Mapper.LONG.transform(rs)
                    .map(val -> Commands.PgVersion.builder()
                            .numeric(val)
                            .build()
                    )
            )
            .build();
}
