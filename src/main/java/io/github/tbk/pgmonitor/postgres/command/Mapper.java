package io.github.tbk.pgmonitor.postgres.command;

import rx.Observable;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface Mapper<T> {
    Observable<T> transform(ResultSet resultSet);

    Mapper<String> STRING = rs -> {
        try {
            return Observable.just(rs.getString(1));
        } catch (SQLException e) {
            return Observable.error(e);
        }
    };

    Mapper<Long> LONG = rs -> {
        try {
            return Observable.just(rs.getLong(1));
        } catch (SQLException e) {
            return Observable.error(e);
        }
    };

    Mapper<Integer> INT = rs -> {
        try {
            return Observable.just(rs.getInt(1));
        } catch (SQLException e) {
            return Observable.error(e);
        }
    };
}
