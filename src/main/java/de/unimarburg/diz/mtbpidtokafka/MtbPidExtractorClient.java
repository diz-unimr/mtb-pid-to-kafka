/*
 This file is part of MTB-PID-TO-KAFKA.

MTB-PID-TO-KAFKA - Get a CSV als Plane Text from MTB (Onkostar), extract the PIDs from the csv file, search all the oder_ids for each PID in NexusDB and produce the info as JSON in a Kafka
topic.

Copyright (C) 2024  Datenintegrationszentrum Philipps-Universität Marburg

MTB-PID-TO-KAFKA  is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

MTB-ID-TO-KAFKA is distributed in the hope that it will be useful,
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
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.web.client.RestTemplate;

import java.sql.Array;
import java.util.HashMap;
import java.util.Objects;

@Component
public class MtbPidExtractorClient {
    private static final Logger log = LoggerFactory.getLogger(MtbPidExtractorClient.class);

    private final String apiUrl;
    private final String username;
    private final String password;

    @Autowired
    public MtbPidExtractorClient(@Value("${services.mtbSender.get-url}") String apiUrl,
                                 @Value("${services.mtbSender.mtb-username}") String username,
                                 @Value("${services.mtbSender.mtb-password}") String password){
        this.apiUrl= apiUrl;
        this.username = username;
        this.password = password;
    }
    private static ResponseEntity<String> responseEntity;

    private final RetryTemplate retryTemplate = defaultTemplate();

    public  String [] mtbPidsExtractor() {
        log.debug("Starting");
        String [] pids = new String[0];
        String authHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.TEXT_PLAIN_VALUE);
        headers.set("Authorization", authHeaderValue);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        // Create GET request to the API
        try {
            responseEntity = retryTemplate.execute(ctx -> restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class));
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.debug("API request succeeded");
            // Parse the CSV response to extract IDs
                String[] lines = Objects.requireNonNull(responseEntity.getBody()).split("\\r?\\n");
                pids = new String[lines.length - 1];
                for (int i = 1; i < lines.length; i++) {
                    String[] columns = lines[i].split(",");
                    pids[i - 1] = columns[0];
                }
            }
        } catch (RestClientException e){
            log.error("API request unsuccessful due to restclientexception");
        }
    return pids;
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
        retryTemplate.registerListener(new RetryListener() {
            @Override
            public <T, E extends Throwable> void onError(RetryContext context,
                                                         RetryCallback<T, E> callback, Throwable throwable) {
                log.warn("HTTP Error occurred: {}. Retrying {}", throwable.getMessage(),
                        context.getRetryCount());
            }
        });
        return retryTemplate;
    }
}
