package com.duoqlo.duoqlostore.model;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataAccessObject<T> {
    protected Connection getConnection() throws SQLException {
        return ConnectDB.connect();
    }

    public abstract void insert(T obj);
    public abstract void delete(T obj);
    public abstract int getID(T obj);
}
