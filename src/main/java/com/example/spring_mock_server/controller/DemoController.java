package com.example.spring_mock_server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class DemoController {
    @Value("${api.url.helloWorld}")
    private String helloWorldURL;

    private final RestTemplate restTemplate;

    @GetMapping("/rest-api")
    public ResponseEntity<String> restApi() {
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, URI.create(helloWorldURL));
        String responseBody = restTemplate.exchange(requestEntity, String.class).getBody();
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/soap-api")
    public ResponseEntity<String> soapApi(@RequestBody String requestBody) {
        RequestEntity<String> requestEntity = new RequestEntity<>(requestBody, HttpMethod.POST, URI.create(helloWorldURL));
        String responseBody = restTemplate.exchange(requestEntity, String.class).getBody();
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }
}