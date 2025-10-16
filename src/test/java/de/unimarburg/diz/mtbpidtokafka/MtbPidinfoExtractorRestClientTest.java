package de.unimarburg.diz.mtbpidtokafka;

import de.unimarburg.diz.mtbpidtokafka.model.MtbPatientInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class MtbPidinfoExtractorRestClientTest {

    private MockRestServiceServer mockRestServiceServer;
    private MtbPidInfoExtractorRestClient out;

    @BeforeEach
    public void setup() {
        var restTemplate = new RestTemplate();
        this.out = new MtbPidInfoExtractorRestClient("http://localhost", "username", "password", restTemplate);
        this.mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldRequestPatientPids() throws Exception {
        final var responseBody = Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("test-response_1.csv")).readAllBytes();

        this.mockRestServiceServer
                .expect(
                        requestTo("http://localhost")
                ).andRespond(
                        withSuccess(responseBody, MediaType.TEXT_PLAIN)
                );

        final var actual = this.out.extractMtbPidInfo();
        assertThat(actual).hasSize(2);

        final var expectedMtbPatientInfos = List.of(
                generateMtbPatientInfo("1"),
                generateMtbPatientInfo("2")
        );
        assertThat(actual).containsAll(expectedMtbPatientInfos);
    }

    @Test
    void shouldReturnEmptyListOnThreeTimesRetryFailure() {
        this.mockRestServiceServer
                .expect(
                        ExpectedCount.times(3),
                        requestTo("http://localhost")
                ).andRespond(
                        withResourceNotFound()
                );

        var actual = this.out.extractMtbPidInfo();
        assertThat(actual).isEmpty();
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
        mtbPatientInfo.setDiagnoseDatum(String.format("2025-10-0%s", id));
        mtbPatientInfo.setMigReferenzTumorId(id);
        return mtbPatientInfo;
    }

}
