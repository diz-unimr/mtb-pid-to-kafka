package de.unimarburg.diz.mtbpidtokafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
@Component
public class MtbPidNexusIdMapper {
    private static final Logger log = LoggerFactory.getLogger(MtbPidExtractorClient.class);
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String schema_table;

    @Autowired
    public MtbPidNexusIdMapper(@Value("${services.mtbSender.nexusdb-url}") String jdbcUrl, @Value("${services.mtbSender.nexusdb-username}") String username,
                               @Value("${services.mtbSender.nexusdb-password}") String password, @Value("${services.mtbSender.nexusdb-schema-table}") String schema_table) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.schema_table = schema_table;
    }

    private static ResultSet resultSet;

    public ResultSet mapMtbPidtoOderId(String[] pids) {

        try {
            // Establishing a connection to the database
            Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
            // SQL query
            // Creating a statement object
            Statement stmt = conn.createStatement();
            // Executing the query
            resultSet = stmt.executeQuery(createCustomSql(pids));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }


    public String createCustomSql(String[] pids) {
        try {
            if (pids.length > 0) {
                StringBuilder sql = new StringBuilder("SELECT * FROM " + schema_table + " WHERE pid IN (");
                for (int i = 0; i < pids.length; i++) {
                    sql.append("'").append(pids[i]).append("'");
                    if (i < pids.length - 1) {
                        sql.append(", ");
                    }
                }
                sql.append(")");
                return sql.toString();
            }

        } catch (NullPointerException e) {
            log.debug("Array with pid is empty");
        }
    return null;
    }
}
