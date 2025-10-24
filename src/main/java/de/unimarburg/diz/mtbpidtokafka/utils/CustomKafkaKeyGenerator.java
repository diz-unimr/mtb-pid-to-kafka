package de.unimarburg.diz.mtbpidtokafka.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomKafkaKeyGenerator {

    private static final Logger log = LoggerFactory.getLogger(CustomKafkaKeyGenerator.class);

    CustomKafkaKeyGenerator() {
        // Intentional empty
    }

    public static String generateCustomPatientIdentifier(String einsendenummer, String patientenId) {

        if (!StringUtils.hasText(patientenId)) {
            throw new IllegalArgumentException("Invalid patientenId");
        }

        if (StringUtils.hasText(einsendenummer)) {
            final var pattern1 = Pattern.compile("(?<prefix>[A-Z])/\\d{2}(?<year>\\d{2})/0*(?<number>\\d+)");
            final var matcher1 = pattern1.matcher(einsendenummer);

            final var pattern2 = Pattern.compile("(?<prefix>[A-Z])0*(?<number>\\d+)-(?<year>\\d{2})");
            final var matcher2 = pattern2.matcher(einsendenummer);

            if (matcher1.find()) {
                return keyFromMatcher(matcher1, patientenId);
            } else if (matcher2.find()) {
                return keyFromMatcher(matcher2, patientenId);
            }
        }
        throw new IllegalArgumentException("Invalid einsendenummer");
    }

    private static String keyFromMatcher(Matcher matcher, String patientenId) {
        final var prefix = matcher.group("prefix");
        final var year = matcher.group("year");
        final var number = matcher.group("number");

        return String.format("%s%s-%s_PID%s", prefix, number, year, patientenId);
    }

}
