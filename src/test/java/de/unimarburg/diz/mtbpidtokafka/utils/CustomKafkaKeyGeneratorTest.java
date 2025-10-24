package de.unimarburg.diz.mtbpidtokafka.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomKafkaKeyGeneratorTest {

    @ParameterizedTest
    @CsvSource({
            "H/2025/54321,01234567,H54321-25_PID01234567",
            "X/2025/654321,01234567,X654321-25_PID01234567",
            "H/1925/7654321,01234567,H7654321-25_PID01234567",
            "H/2025/01234,12345678,H1234-25_PID12345678",
            "H54321-25,01234567,H54321-25_PID01234567",
            "X654321-25,01234567,X654321-25_PID01234567",
            "H7654321-25,01234567,H7654321-25_PID01234567",
            "H01234-25,12345678,H1234-25_PID12345678",
            "NPX.H/2025/012345.100,12345678,H12345-25_PID12345678"
    })
    void shouldGenerateCustomPatientIdentifier(String einsendenummer, String patientId, String expectedKey) {
        assertThat(CustomKafkaKeyGenerator.generateCustomPatientIdentifier(einsendenummer, patientId)).isEqualTo(expectedKey);
    }

    @ParameterizedTest
    @MethodSource("illegalInputs")
    void shouldThrowExceptionOnInvalidInput(String einsendenummer, String patientId, String expectedMessage) {
        var ex = assertThrows(
                IllegalArgumentException.class,
                () -> CustomKafkaKeyGenerator.generateCustomPatientIdentifier(einsendenummer, patientId)
        );

        assertThat(ex.getMessage()).isEqualTo(expectedMessage);
    }

    public static Stream<Arguments> illegalInputs() {
        return Stream.of(
                Arguments.of(null, "012345", "Invalid einsendenummer"),
                Arguments.of("", "012345", "Invalid einsendenummer"),
                Arguments.of("  ", "012345", "Invalid einsendenummer"),
                Arguments.of("H/2025/12345", null, "Invalid patientenId"),
                Arguments.of("H/2025/12345", "", "Invalid patientenId"),
                Arguments.of("H/2025/12345", "   ", "Invalid patientenId"),
                Arguments.of(null, null, "Invalid patientenId")
        );
    }

}
