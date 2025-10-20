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
import de.unimarburg.diz.mtbpidtokafka.model.MtbPatientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@EnableScheduling
@Component
public class MtbPidToKafkaScheduler {

    private static final Logger log = LoggerFactory.getLogger(MtbPidToKafkaScheduler.class);

    private final MtbPidNexusIdKafkaProducer mtbPidNexusIdKafkaProducer;
    private final MtbPidInfoExtractorRestClient mtbPidExtractorClient;

    @Autowired
    public MtbPidToKafkaScheduler(
            final MtbPidNexusIdKafkaProducer mtbPidNexusIdKafkaProducer,
            final MtbPidInfoExtractorRestClient mtbPidExtractorClient
    ) {
        this.mtbPidNexusIdKafkaProducer = mtbPidNexusIdKafkaProducer;
        this.mtbPidExtractorClient = mtbPidExtractorClient;
    }

    @Scheduled(fixedRateString = "${services.mtbSender.mtb-fetch-metrics}")// Repeate the process in defined seconds
    public void launchTaskMtbPidToKafka() throws SQLException, JsonProcessingException {
        log.info("mtb-pid-to-kafka process restarting ....");
        List<MtbPatientInfo> listMtbPidInfo = mtbPidExtractorClient.extractMtbPidInfo();
        mtbPidNexusIdKafkaProducer.sendToKafka(listMtbPidInfo);
    }
}
