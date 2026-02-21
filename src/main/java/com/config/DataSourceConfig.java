package com.config;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceConfig {

    public static DataSource getDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(System.getenv("DB_URL"));
        ds.setUsername(System.getenv("DB_USER"));
        ds.setPassword(System.getenv("DB_PASS"));
        return ds;
    }
}
