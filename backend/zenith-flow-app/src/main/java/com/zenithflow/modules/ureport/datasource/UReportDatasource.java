package com.zenithflow.modules.ureport.datasource;

import com.bstek.ureport.definition.datasource.BuildinDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * UReport默认数据源
 *
 *
 */
@Component
public class UReportDatasource implements BuildinDatasource {
    @Autowired
    private DataSource dataSource;

    @Override
    public String name() {
        return "默认数据源";
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
