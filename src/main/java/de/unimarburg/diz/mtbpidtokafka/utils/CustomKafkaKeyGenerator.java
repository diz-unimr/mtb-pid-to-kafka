package de.unimarburg.diz.mtbpidtokafka.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class CustomKafkaKeyGenerator {
    private static final Logger log = LoggerFactory.getLogger(CustomKafkaKeyGenerator.class);
    public static String generateCustomPatientIdentifier(String einsendenummer, String patientenId) {

        if (StringUtils.hasText(einsendenummer) && StringUtils.hasText(patientenId)) {
            log.info("Einsendenummer is not null");

            // Split the einsendenummer by '/'
            if (einsendenummer.contains("/")) {
                String[] parts = einsendenummer.split("/");

                // Check if the prefix is "H"
                String prefix = parts[0]; // Assuming the first part is the prefix
                if (!prefix.equals("H")) {
                    throw new IllegalArgumentException("The prefix must be 'H'. Provided: " + prefix);
                }

                // Extract the required parts
                String firstPart = parts[parts.length - 1]; // Last part

                String secondPart = parts.length > 1 ? parts[parts.length - 2] : "0";

                // Convert to unsigned integers
                int firstNumber = Integer.parseInt(firstPart);
                // Extract the last two characters after the leading "0" from the second part
                String secondNumber = secondPart.length() > 1 ? secondPart.substring(2) : "0";

                // Construct the custom patient identifier
                return prefix + firstNumber + "-" + secondNumber + "_PID" + patientenId;
            }
            throw new IllegalArgumentException("The einsendennummer is not valid");
        }
        return "no journal or pid number present";
    }
}
