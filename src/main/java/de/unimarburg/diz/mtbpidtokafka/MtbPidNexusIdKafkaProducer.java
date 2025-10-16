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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimarburg.diz.mtbpidtokafka.model.MtbPatientInfo;
import de.unimarburg.diz.mtbpidtokafka.utils.CustomDateFormatter;
import de.unimarburg.diz.mtbpidtokafka.utils.CustomKafkaKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Configuration
@EnableKafka
public class MtbPidNexusIdKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(MtbPidNexusIdKafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MtbPidNexusIdKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, @Value("${spring.kafka.producer.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        kafkaTemplate.setDefaultTopic(topic);
    }

    public void sendToKafka(final List<MtbPatientInfo> listMtbPidInfo) throws JsonProcessingException {
        for (MtbPatientInfo mtbPatientInfo : listMtbPidInfo) {
            String key = CustomKafkaKeyGenerator.generateCustomPatientIdentifier(mtbPatientInfo.getEinsendennummer(), mtbPatientInfo.getPatientenId());
            mtbPatientInfo.setDiagnoseDatum(CustomDateFormatter.convertDateFormat(mtbPatientInfo.getDiagnoseDatum()));// Example: using field1 as the key
            String message = objectMapper.writeValueAsString(mtbPatientInfo);
            kafkaTemplate.sendDefault(key, message);
            log.info("Message sent to kafka ");
        }
    }
}

