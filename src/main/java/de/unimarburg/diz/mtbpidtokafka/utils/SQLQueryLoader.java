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
package de.unimarburg.diz.mtbpidtokafka.utils;

import de.unimarburg.diz.mtbpidtokafka.MtbPidExtractorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class SQLQueryLoader {
    private static final String YAML_FILE = "sql/sql_queries.yml";
    private static final Logger log = LoggerFactory.getLogger(MtbPidExtractorClient.class);
    public static Map<String, String> loadQueries() {
        Yaml yaml = new Yaml();
        try {
            File initialFile = new File(YAML_FILE);
            InputStream inputStream= new FileInputStream(initialFile);
            log.info(inputStream.toString());
            return yaml.load(inputStream);
        } catch (Exception e){
           log.error(e.getMessage());
           log.info("Error while reading file");
        }
        return null;
    }
}