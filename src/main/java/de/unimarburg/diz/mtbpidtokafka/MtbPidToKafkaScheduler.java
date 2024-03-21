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

    @Scheduled(fixedRate = 5000)// Repeate the process in every 4 sec
    public void launchTaskMtbPidToKafka() throws SQLException {
        mtbPidNexusIdKafkaProducer.sendToKafka();
    }
}
