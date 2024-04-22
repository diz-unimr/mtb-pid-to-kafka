/*
This file is part of MTB-PID-TO-KAFKA.

MTB-PID-TO-KAFKA - Get a CSV als Plane Text from MTB (Onkostar), extract the PIDs from the csv file, search all the oder_ids for each PID in NexusDB and produce the info as JSON in a Kafka
topic.

Copyright (C) 2024  Datenintegrationszentrum Philipps-Universität Marburg

MTB-PID-TO-KAFKA  is free software: you can redistribute it and/or modify
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

import de.unimarburg.diz.mtbpidtokafka.utils.SQLQueryLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Arrays;
import java.util.Map;

import static de.unimarburg.diz.mtbpidtokafka.utils.StringCreatorFromArray.createInStringPidsArray;

@Component
public class MtbPidNexusIdMapper {
    private static final Logger log = LoggerFactory.getLogger(MtbPidExtractorClient.class);
    private final String jdbcUrl;
    private final String username;
    private final String password;

    @Autowired
    public MtbPidNexusIdMapper(@Value("${services.mtbSender.nexusdb-url}") String jdbcUrl, @Value("${services.mtbSender.nexusdb-username}") String username,
                               @Value("${services.mtbSender.nexusdb-password}") String password){
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    private static ResultSet resultSet;
    public ResultSet mapMtbPidtoOderId(String[] pids) {
        try {
            // Establishing a connection to the database
            Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
            Map<String, String> queries = SQLQueryLoader.loadQueries();
            assert queries != null;
            String selectOrderNumberByPid = queries.get("selectOrderNumberByPid");
            // Create a new sql statement
            String customsql = createInStringPidsArray(pids,selectOrderNumberByPid);
            PreparedStatement preparedStatement = conn.prepareStatement(customsql);
            // Set parameters
            for (int i = 0; i < pids.length ; i++) {
                log.info(pids[i]);
                preparedStatement.setString(i + 1, pids[i]);
            }
            resultSet = preparedStatement.executeQuery();
            return resultSet;

        } catch (SQLException | NullPointerException e) {
            log.error(e.getMessage());
        }

        return resultSet;
    }


}
