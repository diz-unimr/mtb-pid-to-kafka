package de.unimarburg.diz.mtbpidtokafka.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomDateFormatter {
    private static final Logger log = LoggerFactory.getLogger(CustomDateFormatter.class);

    public static String convertDateFormat(String givenDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate date = LocalDate.parse(givenDate, inputFormatter);
            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            // Handle the exception
            log.error(e.getMessage());
            return null;
        }
    }
}
