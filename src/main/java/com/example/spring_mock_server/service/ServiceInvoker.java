package com.example.spring_mock_server.service;

import com.example.spring_mock_server.exception.EmptyValueException;
import com.example.spring_mock_server.exception.InvalidNumericException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ServiceInvoker {

    private final RestTemplate restTemplate;

    @Value("${external.service.url}")
    private String externalServiceUrl;

    public String invoke(String value) throws RuntimeException {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(externalServiceUrl, value, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
             if (e.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                throw new EmptyValueException("the input value is empty");
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidNumericException("the input value is numeric");
            } else {
                throw new RuntimeException("An error occurred while calling the service", e);
            }
        } catch (RestClientException e) {
            throw new RuntimeException("An error occurred while calling the service", e);
        }
    }
}

