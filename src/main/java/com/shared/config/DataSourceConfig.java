package com.shared.config;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceConfig {

    private DataSourceConfig() {
        throw new UnsupportedOperationException("Clase utilitaria - no instanciable");
    }

    public static DataSource getDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(System.getenv("DB_URL"));
        ds.setUsername(System.getenv("DB_USER"));
        ds.setPassword(System.getenv("DB_PASS"));
        return ds;
    }
}
