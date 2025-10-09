package de.unimarburg.diz.mtbpidtokafka.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class CustomKafkaKeyGenerator {
    private static final Logger log = LoggerFactory.getLogger(CustomKafkaKeyGenerator.class);
    public static String generateCustomPatientIdentifier(String einsendenummer, String patientenId) {

        if (StringUtils.hasText(einsendenummer) && StringUtils.hasText(patientenId)) {
            log.debug("Einsendenummer is not null");

            final var pattern = Pattern.compile("(?<prefix>[A-Z])/\\d{2}(?<year>\\d{2})/0*(?<number>\\d+)");
            final var matcher = pattern.matcher(einsendenummer);

            if (matcher.find()) {
                final var prefix = matcher.group("prefix");
                final var year = matcher.group("year");
                final var number = matcher.group("number");

                if (!prefix.equals("H")) {
                    log.error(String.format("The prefix must be 'H'. Provided: %s", prefix));
                }
                
                return String.format("%s%s-%s_PID%s", "H", number, year, patientenId);
            }
            log.error("The einsendennummer is not valid");
        }
        return "no journal or pid number present";
    }
}