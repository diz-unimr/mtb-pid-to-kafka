/*
 This file is part of KAFKA-TO-BWHC.

 KAFKA-TO-BWHC - Read MTB-file from a Apache Kafka topic > send MTB-file via REST to DIZ Marburg  BWHC Node >
 produce the HTTP Response to a new Apache Kafka topic
 Copyright (C) 2023  Datenintegrationszentrum Philipps-Universität Marburg

 KAFKA-TO-BWHC is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 KAFKA-TO-BWHC is distributed in the hope that it will be useful,
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

    public void sendToKafka() throws SQLException {

        String[] pids = mtbPidExtractorClient.mtbPidsExtractor();
        if (pids.length == 0) {
            return;
        }
        try (ResultSet resultSet = mtbPidNexusIdMapper.mapMtbPidtoOderId(pids)) {
            while (resultSet.next()) {
                MtbPidNexusOderId mtbPidNexusOderId = new MtbPidNexusOderId();
                String pid = resultSet.getString("pid");
                String oder_id = resultSet.getString("oder_id");
                mtbPidNexusOderId.setPid(pid);
                kafkaTemplate.sendDefault(oder_id, mtbPidNexusOderId);
            }
        }
    }
}
