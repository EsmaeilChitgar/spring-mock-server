package com.example.spring_mock_server.all_in_one;

import org.junit.jupiter.api.*;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpForward;
import org.mockserver.verify.VerificationTimes;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpForward.forward;

public class AllInOneTest {

    private ClientAndServer mockServer;

    @BeforeEach
    public void startServer() {
        mockServer = ClientAndServer.startClientAndServer(1093);
    }

    @AfterEach
    public void stopServer() {
        mockServer.stop();
    }

    @Test
    public void testFixedResponse() {
        mockServer.when(request()
                        .withMethod("GET")
                        .withPath("/api/test"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody("This is a fixed response"));
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:1093/api/test", String.class);
        assertThat(result).isEqualTo("This is a fixed response");
    }

    @Test
    @Disabled
    public void testRequestForwarding() {
        mockServer.when(request()
                                .withMethod("GET")
                                .withPath("/index.html"),
                        exactly(1))
                .forward(forward()
                        .withHost("www.mock-server.com")
                        .withPort(80)
                        .withScheme(HttpForward.Scheme.HTTP));
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:1093/index.html", String.class);
        assertThat(result).isEqualTo("Expected response from example.com");
    }

    @Test
    public void testCallback() {
        mockServer.when(request()
                        .withMethod("POST")
                        .withPath("/api/callback"))
                        .respond(httpRequest -> {
            if (httpRequest.getPath().getValue().endsWith("/callback")) {
                return response()
                        .withStatusCode(200)
                        .withBody("Callback executed with body: " + httpRequest.getBodyAsString());
            } else {
                return notFoundResponse();
            }
        });
        RestTemplate restTemplate = new RestTemplate();
        String requestBody = "hello callback";
        String result = restTemplate.postForObject("http://localhost:1093/api/callback", requestBody, String.class);
        assertThat(result).isEqualTo("Callback executed with body: " + requestBody);
    }

    @Test
    public void testRequestVerification() {
        mockServer.when(request()
                        .withMethod("POST")
                        .withPath("/api/verify")).
                respond(response()
                        .withStatusCode(200)
                        .withBody("Verification Success"));
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject("http://localhost:1093/api/verify", null, String.class);
        mockServer.verify(request()
                        .withMethod("POST")
                        .withPath("/api/verify"),
                VerificationTimes.exactly(1));
        assertThat(result).isEqualTo("Verification Success");
    }

    @Test
    public void testSOAPService() {
        String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://example.com/Service\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ser:TestRequest>" +
                "<ser:input>Test Input</ser:input>" +
                "</ser:TestRequest>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        String soapResponse = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://example.com/Service\">" +
                "<soapenv:Header/>" +
                "<soapenv:Body>" +
                "<ser:TestResponse>" +
                "<ser:output>Test Output</ser:output>" +
                "</soapenv:Body>" +
                "</soapenv:Envelope>";
        mockServer.when(request()
                        .withMethod("POST")
                        .withPath("/api/soap")
                        .withBody(soapRequest))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader("Content-Type", "text/xml; charset=utf-8")
                        .withBody(soapResponse));
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.postForObject("http://localhost:1093/api/soap", soapRequest, String.class);
        assertThat(result).isEqualTo(soapResponse);
    }

    @Test
    public void testWithHeader() {
        mockServer.when(request()
                        .withMethod("POST")
                        .withPath("/validate")
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"username\": \"foo\", \"password\": \"bar\"}")
                )
                .respond(response()
                        .withStatusCode(200)
                        .withHeaders(
                                new Header("Content-Type", "application/json; charset=utf-8"),
                                new Header("Cache-Control", "public, max-age=86400")
                        )
                        .withBody("{\"message\": \"valid username and password\"}")
                        .withDelay(TimeUnit.SECONDS, 1));
        String requestBody = "{\"username\": \"foo\", \"password\": \"bar\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:1093/validate",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("valid username and password");
    }
}