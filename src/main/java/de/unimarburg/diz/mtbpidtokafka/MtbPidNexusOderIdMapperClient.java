/*
 This file is part of KAFKA-TO-BWHC.

 KAFKA-TO-BWHC - Read MTB-file from a Apache Kafka topic > send MTB-file via REST to DIZ Marburg  BWHC Node >
 produce the HTTP Response to a new Apache Kafka topic
 Copyright (C) 2023  Datenintegrationszentrum Philipps-Universität Marburg

 KAFKA-TO-BWHC is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 KAFKA-TO-BWHC is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package de.unimarburg.diz.mtbpidtokafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;

@Component
public class MtbPidNexusOderIdMapperClient {
    private static final Logger log = LoggerFactory.getLogger(MtbPidNexusOderIdMapperClient.class);

    private final String apiUrl;
    private final String username;
    private final String password;

    @Autowired
    public MtbPidNexusOderIdMapperClient(@Value("${services.mtbSender.get_url}") String apiUrl,
                                         @Value("${services.mtbSender.mtb_username}") String username,
                                         @Value("${services.mtbSender.mtb_password}") String password){
        this.apiUrl= apiUrl;
        this.username = username;
        this.password = password;
    }

    public  String [] mtbPidsExtractor() {
        log.debug("Starting");
        String authHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.TEXT_PLAIN_VALUE);
        headers.set("Authorization", authHeaderValue);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        // Create GET request to the API
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse the CSV response to extract IDs
            String[] lines = response.getBody().split("\\r?\\n");
            String[] ids = new String[lines.length - 1]; // First line is header
            for (int i = 1; i < lines.length; i++) {
                String[] columns = lines[i].split(",");
                ids[i - 1] = columns[0]; // Assuming ID is the first column
            }
            System.out.println(ids.toString());
            return ids;
        } else {
            System.err.println("Failed to fetch CSV file. Status code: " + response.getStatusCode().value());
            return new String[0];
        }
    }

    public static RetryTemplate defaultTemplate(){
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(5000);
        backOffPolicy.setMultiplier(1.25);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        HashMap<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(RestClientException.class,true);
        RetryPolicy retryPolicy = new SimpleRetryPolicy(3, retryableExceptions);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
}
