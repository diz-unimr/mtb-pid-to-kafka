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

import de.unimarburg.diz.mtbpidtokafka.model.MtbPidNexusOderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;


@Service
@Configuration
@EnableKafka
public class MtbPidNexusIdKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(MtbPidNexusIdKafkaProducer.class);
    private final MtbPidExtractorClient mtbPidExtractorClient;
    private final KafkaTemplate<String, MtbPidNexusOderId> kafkaTemplate;
    private final MtbPidNexusIdMapper mtbPidNexusIdMapper;
    @Value("${spring.kafka.producer.topic}")
    private final String mtb = "mtb-pid-nexus-oder-id";

    @Autowired
    public MtbPidNexusIdKafkaProducer(MtbPidExtractorClient mtbPidExtractorClient, MtbPidNexusIdMapper mtbPidNexusIdMapper, KafkaTemplate<String, MtbPidNexusOderId> kafkaTemplate) {
        this.mtbPidExtractorClient = mtbPidExtractorClient;
        this.mtbPidNexusIdMapper = mtbPidNexusIdMapper;
        this.kafkaTemplate = kafkaTemplate;
        kafkaTemplate.setDefaultTopic(mtb);
    }

    public String findTumorID(String[][] result, String value) {
        if (result == null || result.length < 2) {
            return null; // Return null if the input is invalid
        }
        String[] pids = result[0];
        String[] tumorIds = result[1];
        for (int i = 0; i < pids.length; i++) {
            if (pids[i].equals(value)) {
                return tumorIds[i];
            }
        }
        return null;
    }

    public void sendToKafka() throws SQLException {
        String[][] result = mtbPidExtractorClient.mtbPidsExtractor();
        String[] pids = result[0];
        String[] tumorIds = result[1];

        if (pids.length == 0 && tumorIds.length == 0) {
            log.info("No PIDs und tumorIds are found in the result");
            return;
        }
        try (ResultSet resultSet = mtbPidNexusIdMapper.mapMtbPidtoOderId(pids)) {
            if (resultSet == null) {
                log.info("Resultset size is null");
            return;
            }
            while (resultSet.next()) {
                MtbPidNexusOderId mtbPidNexusOderId = new MtbPidNexusOderId();
                String pid = resultSet.getString("pid");
                String tumorId = findTumorID(result, pid);
                String orderId = resultSet.getString("oder_id");
                mtbPidNexusOderId.setPid(pid);
                mtbPidNexusOderId.setTumorId(tumorId);
                mtbPidNexusOderId.setOrderId(orderId);
                kafkaTemplate.sendDefault(orderId, mtbPidNexusOderId);
                log.info("Message sent to kafka ");
                }
            }
        }
}

