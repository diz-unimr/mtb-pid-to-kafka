package de.unimarburg.diz.mtbpidtokafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;

import static de.unimarburg.diz.mtbpidtokafka.utils.StringCreatorFromArray.createInStringPidsArray;

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
        return "select\n" +
                "    medorder.OrderNumber as oder_id,\n" +
                "    life_number.Number as pid\n" +
                "from dbo.LifeNumber as life_number\n" +
                "         inner join\n" +
                "     dbo.medcase as medcase on life_number.Patient = medcase.Patient\n" +
                "         inner join  dbo.MedOrder as medorder on medcase.GUID = medorder.MedCase\n" +
                "         inner join dbo.Client as client on client.GUID = MedCase.MedCaseClient and client.Name = 'XXX'\n" +
                "where life_number.Number in " + createInStringPidsArray(pids);
    }

}
