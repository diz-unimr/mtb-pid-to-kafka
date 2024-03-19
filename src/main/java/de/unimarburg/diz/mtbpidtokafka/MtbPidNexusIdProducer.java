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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutionException;



@Service
@EnableKafka
@Configuration
public class MtbPidNexusIdProducer {
    private static final Logger log = LoggerFactory.getLogger(MtbPidNexusIdProducer.class);
    private final MtbPidNexusOderIdMapperClient mtbPidNexusOderIdMapperClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.producer.topic}")
    private final String mtb = "mtb-pid-nexus-oder-id";


    @Autowired
    public MtbPidNexusIdProducer(MtbPidNexusOderIdMapperClient mtbPidNexusOderIdMapperClient, KafkaTemplate<String, String> kafkaTemplate){
        this.mtbPidNexusOderIdMapperClient = mtbPidNexusOderIdMapperClient;
        this.kafkaTemplate = kafkaTemplate;
        kafkaTemplate.setDefaultTopic(mtb);

    }

    public boolean sendToKafka(String key, String data)
            throws InterruptedException, ExecutionException {
        var result = kafkaTemplate.sendDefault(key, data);

        if (result.get() != null) {
            log.debug("stored msg : " + data);
        } else {
            log.error("failed! send data: " + data);
            return false;
        }

        return true;
    }

    protected boolean processMtbFile()
            throws InterruptedException, ExecutionException{
        try {

            final String key = "test";

            sendToKafka(key, "123");
        } catch (InterruptedException | ExecutionException e) {
            // Handle serialization errors
            log.error("failed send data to kafka", e);
            throw e;
        }
        return true;
    }
}
