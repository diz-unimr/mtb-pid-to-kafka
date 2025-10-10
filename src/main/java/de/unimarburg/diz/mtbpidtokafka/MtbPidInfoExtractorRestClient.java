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

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import de.unimarburg.diz.mtbpidtokafka.model.MtbPatientInfo;
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
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
public class MtbPidInfoExtractorRestClient {
    private static final Logger log = LoggerFactory.getLogger(MtbPidInfoExtractorRestClient.class);

    private final String apiUrl;
    private final String username;
    private final String password;

    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;

    @Autowired
    public MtbPidInfoExtractorRestClient(@Value("${services.mtbSender.get-url}") String apiUrl,
                                         @Value("${services.mtbSender.mtb-username}") String username,
                                         @Value("${services.mtbSender.mtb-password}") String password,
                                         final RestTemplate restTemplate
    ){
        this.apiUrl= apiUrl;
        this.username = username;
        this.password = password;

        this.restTemplate = restTemplate;
        this.retryTemplate = defaultTemplate();
    }

    public List<MtbPatientInfo>  mtbPidInfoExtractor() {
        log.debug("Starting");
        String[][] result = new String[2][0];

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE);
        headers.setBasicAuth(username, password);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        // Create GET request to the API
        try {
            ResponseEntity<String> responseEntity = retryTemplate.execute(ctx -> restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class));
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                log.debug("API request succeeded");
                System.out.println(responseEntity.getBody());
                Reader reader = new InputStreamReader(new ByteArrayInputStream(Objects.requireNonNull(responseEntity.getBody()).getBytes()));
                CsvToBean<MtbPatientInfo> csvToBean = new CsvToBeanBuilder<MtbPatientInfo>(reader)
                        .withType(MtbPatientInfo.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .withSeparator(';') // Set the delimiter to semicolon
                        .withQuoteChar('"').build();
                return csvToBean.parse();
            }
        } catch (RestClientException e){
            log.error("API request unsuccessful due to restclientexception", e);
        }
        return List.of();
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
