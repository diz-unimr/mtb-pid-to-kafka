/*
 This file is part of MTB-ID-TO-KAFKA.

MTB-ID-TO-KAFKA - Get a CSV als Plane Text from MTB (Onkostar), extract the PIDs from the csv file, search all the oder_ids for each PID in NexusDB and produce the info as JSON in a Kafka
topic.

Copyright (C) 2024  Datenintegrationszentrum Philipps-Universität Marburg

MTB-ID-TO-KAFKA  is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

MTB-ID-TO-KAFKA is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
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
