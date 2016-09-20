package io.github.tbk.postrics.postgres.command;

public interface Command<T> {
    String sql();

    Mapper<T> mapper();

    CommandImpl.CommandImplBuilder<Long> GET_LONG = CommandImpl.<Long>builder()
            .mapper(Mapper.LONG);

    CommandImpl.CommandImplBuilder<String> GET_STRING = CommandImpl.<String>builder()
            .mapper(Mapper.STRING);
}
