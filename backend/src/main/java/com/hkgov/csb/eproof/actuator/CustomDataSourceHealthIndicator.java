package com.hkgov.csb.eproof.actuator;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class CustomDataSourceHealthIndicator extends DataSourceHealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private final HikariDataSource dataSource;

    public CustomDataSourceHealthIndicator(DataSource dataSource) {
        super(dataSource);
        this.dataSource = new HikariDataSource((HikariDataSource) dataSource);
        this.dataSource.setConnectionTimeout(2000);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try (Connection connection = dataSource.getConnection()) {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            builder.up();
        } catch (Exception ex) {
            builder.down(ex);
        }
    }
}