package io.github.tbk.postrics.postgres.command;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetLong implements Command<GetLong.PgVersion> {
    @Override
    public String sql() {
        return "SELECT version()";
    }

    @Override
    public Mapper<PgVersion> mapper() {
        return rs -> Mapper.STRING.transform(rs)
                .map(val -> PgVersion.builder()
                        .full(val)
                        .build());
    }

    @Value
    @Builder
    public static class PgVersion {
        private String full;
        private String value;
        private int numeric;
    }
}
