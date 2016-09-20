package io.github.tbk.postrics.postgres.command;

public class CommandImpl<K> implements Command<K> {
    public static <K> CommandImplBuilder<K> builder() {
        return new CommandImplBuilder<>();
    }

    private final String sql;
    private final Mapper<K> mapper;

    private CommandImpl(String sql, Mapper<K> mapper) {
        this.sql = sql;
        this.mapper = mapper;
    }

    @Override
    public String sql() {
        return sql;
    }

    @Override
    public Mapper<K> mapper() {
        return mapper;
    }

    public static class CommandImplBuilder<K> {
        private String sql;
        private Mapper<K> mapper;

        CommandImplBuilder() {
        }

        public CommandImpl.CommandImplBuilder<K> sql(String sql) {
            this.sql = sql;
            return this;
        }

        public CommandImpl.CommandImplBuilder<K> mapper(Mapper<K> mapper) {
            this.mapper = mapper;
            return this;
        }

        public CommandImpl<K> build() {
            return new CommandImpl<>(sql, mapper);
        }

        public String toString() {
            return "io.github.tbk.postrics.postgres.command.CommandImpl.CommandImplBuilder(sql=" + this.sql + ", mapper=" + this.mapper + ")";
        }
    }
}
