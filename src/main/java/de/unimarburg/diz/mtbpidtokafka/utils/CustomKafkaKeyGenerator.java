package de.unimarburg.diz.mtbpidtokafka.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class CustomKafkaKeyGenerator {
    private static final Logger log = LoggerFactory.getLogger(CustomKafkaKeyGenerator.class);
    public static String generateCustomPatientIdentifier(String einsendenummer, String patientenId) {

        if (StringUtils.hasText(einsendenummer) && StringUtils.hasText(patientenId)) {
            log.debug("Einsendenummer is not null");

            // Split the einsendenummer by '/'
            if (einsendenummer.contains("/")) {
                String[] parts = einsendenummer.split("/");

                // Check if the prefix is "H"
                String prefix = parts[0]; // Assuming the first part is the prefix
                if (!prefix.endsWith("H")) {
                    log.error("The prefix must be 'H'. Provided: " + prefix);
                }


                String secondPart = parts[parts.length - 2];
                // Extract the last two characters after the leading "0" from the second part
                String secondNumber = secondPart.length() > 1 ? secondPart.substring(2) : "0";

                // Split the last part
                // Extract the required parts
                String lastPart = parts[parts.length - 1]; // Last part
                // Convert to unsigned integers
                String [] lastParts = lastPart.split("\\.");
                String lastPartStart = lastParts[0];
                int firstNumber = Integer.parseInt(lastPartStart);

                // Construct the custom patient identifier
                return "H" + firstNumber + "-" + secondNumber + "_PID" + patientenId;
            }
            log.error("The einsendennummer is not valid");
        }
        return "no journal or pid number present";
    }
}
