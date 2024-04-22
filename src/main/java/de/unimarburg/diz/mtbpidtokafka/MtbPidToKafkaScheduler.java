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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@EnableScheduling
@Component
public class MtbPidToKafkaScheduler {
    private final MtbPidNexusIdKafkaProducer mtbPidNexusIdKafkaProducer;

    @Autowired
    public MtbPidToKafkaScheduler(MtbPidNexusIdKafkaProducer mtbPidNexusIdKafkaProducer){
        this.mtbPidNexusIdKafkaProducer = mtbPidNexusIdKafkaProducer;
    }

    @Scheduled(fixedRate = 10000)// Repeate the process in every 10 sec
    public void launchTaskMtbPidToKafka() throws SQLException {
        mtbPidNexusIdKafkaProducer.sendToKafka();
    }
}
