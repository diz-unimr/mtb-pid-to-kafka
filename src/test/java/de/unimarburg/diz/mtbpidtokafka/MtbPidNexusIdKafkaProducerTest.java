package de.unimarburg.diz.mtbpidtokafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimarburg.diz.mtbpidtokafka.model.MtbPatientInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MtbPidNexusIdKafkaProducerTest {

    private MtbPidInfoExtractorRestClient mtbPidInfoExtractorRestClient;
    private KafkaTemplate<String, String> kafkaTemplate;

    private MtbPidNexusIdKafkaProducer out;

    @BeforeEach
    public void setUp(
            @Mock MtbPidInfoExtractorRestClient mtbPidInfoExtractorRestClient,
            @Mock KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.mtbPidInfoExtractorRestClient = mtbPidInfoExtractorRestClient;
        this.kafkaTemplate = kafkaTemplate;

        this.out = new MtbPidNexusIdKafkaProducer(mtbPidInfoExtractorRestClient, kafkaTemplate, "testtopic");
    }

    @Test
    void shouldSendRecordsToKafka() throws Exception {
        var testPatientInfos = List.of(
                generateMtbPatientInfo("1"),
                generateMtbPatientInfo("2")
        );

        var expectedPatientInfoStrings = List.of(
                generateMtbPatientKafkaString("1"),
                generateMtbPatientKafkaString("2")
        );

        when(mtbPidInfoExtractorRestClient.mtbPidInfoExtractor())
                .thenReturn(testPatientInfos);

        out.sendToKafka();

        var keyCaptor = ArgumentCaptor.forClass(String.class);
        var bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(2))
                .sendDefault(keyCaptor.capture(), bodyCaptor.capture());

        assertThat(keyCaptor.getAllValues())
                .hasSize(2);
        assertThat(bodyCaptor.getAllValues())
                .hasSize(2);

        assertThat(keyCaptor.getAllValues())
                .containsExactly("H10000-25_PID10001", "H20000-25_PID10002");
        assertThat(bodyCaptor.getAllValues())
                .containsAll(expectedPatientInfoStrings);
    }

    private MtbPatientInfo generateMtbPatientInfo(String id) {
        MtbPatientInfo mtbPatientInfo = new MtbPatientInfo();
        mtbPatientInfo.setPatientenId(String.format("1000%s", id));
        mtbPatientInfo.setPidGesperrt(Integer.parseInt(id) % 2 == 0 ? 1 : 0);
        mtbPatientInfo.setTumorId(id);
        mtbPatientInfo.setSid(id);
        mtbPatientInfo.setGuid(String.format("00000000-000%s-ffff-ffff-00000000000%s", id, id));
        mtbPatientInfo.setRevision(id);
        mtbPatientInfo.setEinsendennummer(String.format("H/2025/%s0000", id));
        mtbPatientInfo.setDiagnoseDatum(String.format("0%s.10.2025", id));
        mtbPatientInfo.setMigReferenzTumorId(id);
        return mtbPatientInfo;
    }

    private String generateMtbPatientKafkaString(String id) {
        MtbPatientInfo mtbPatientInfo = new MtbPatientInfo();
        mtbPatientInfo.setPatientenId(String.format("1000%s", id));
        mtbPatientInfo.setPidGesperrt(Integer.parseInt(id) % 2 == 0 ? 1 : 0);
        mtbPatientInfo.setTumorId(id);
        mtbPatientInfo.setSid(id);
        mtbPatientInfo.setGuid(String.format("00000000-000%s-ffff-ffff-00000000000%s", id, id));
        mtbPatientInfo.setRevision(id);
        mtbPatientInfo.setEinsendennummer(String.format("H/2025/%s0000", id));
        mtbPatientInfo.setDiagnoseDatum(String.format("2025-10-0%s", id));
        mtbPatientInfo.setMigReferenzTumorId(id);

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(mtbPatientInfo);
        } catch (IOException e) {
            return "";
        }
    }

}
