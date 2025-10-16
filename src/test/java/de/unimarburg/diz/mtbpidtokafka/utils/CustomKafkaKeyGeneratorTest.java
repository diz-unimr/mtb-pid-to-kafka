package de.unimarburg.diz.mtbpidtokafka.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CustomKafkaKeyGeneratorTest {


    @ParameterizedTest
    @CsvSource({
            "H/2025/54321,01234567,H54321-25_PID01234567",
            "X/2025/654321,01234567,H654321-25_PID01234567",
            "H/1925/7654321,01234567,H7654321-25_PID01234567",
            "H/2025/01234,12345678,H1234-25_PID12345678",
            "H54321-25,01234567,H54321-25_PID01234567",
            "X654321-25,01234567,H654321-25_PID01234567",
            "H7654321-25,01234567,H7654321-25_PID01234567",
            "H01234-25,12345678,H1234-25_PID12345678",
            "H202554321,01234567,no journal or pid number present",
    })
    void shouldGenerateCustomPatientIdentifier(String einsendenummer, String patientId, String expectedKey) {
        assertThat(CustomKafkaKeyGenerator.generateCustomPatientIdentifier(einsendenummer, patientId)).isEqualTo(expectedKey);
    }

}