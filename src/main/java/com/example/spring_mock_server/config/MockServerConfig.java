package com.example.spring_mock_server.config;

import com.example.spring_mock_server.helper.HttpHelper;
import com.example.spring_mock_server.helper.ResourceHelper;
import com.example.spring_mock_server.helper.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.http.HttpStatus;

import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MockServerConfig {
    private static final Integer MOCK_SERVER_PORT = 1099;
    private final ClientAndServer clientAndServer;
    public final MockServerClient mockServerClient = new MockServerClient("localhost", MOCK_SERVER_PORT);

    public MockServerConfig() {
        this.clientAndServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
    }

    public void registerHelloWorldEndpoint() {
        mockServerClient.when(request().
                                withMethod("GET")
                                .withPath("/api/helloWorld")
                        /*exactly(1)*/)
                .respond(
                        response().
                                withStatusCode(200)
                                .withBody("Mocked Response!!!")
                                .withDelay(TimeUnit.MILLISECONDS, 10)
                );
    }

    public void verifyHelloWorldEndpoint() {
        mockServerClient.verify(
                request()
                        .withMethod("GET")
                        .withPath("/api/helloWorld")
//                VerificationTimes.exactly(1)
        );
    }

    public void stopServer() {
        clientAndServer.stop();
    }

    public void registerDynamicLengthEndpoint() {
        mockServerClient.when(request()
                        .withMethod("POST")
                        .withPath("/api/dynamicLen"))
                .respond(httpRequest -> {
                    String requestBody = httpRequest.getBodyAsString();
                    if (requestBody == null || requestBody.isEmpty()) {
                        return HttpResponse.response()
                                .withStatusCode(HttpStatus.NOT_ACCEPTABLE.value())
                                .withBody("the input value is null")
                                .withDelay(TimeUnit.MILLISECONDS, 10);
                    }
                    if (Util.isNumeric(requestBody)) {
                        return HttpResponse.response()
                                .withStatusCode(HttpStatus.BAD_REQUEST.value())
                                .withBody("the input value is not integer")
                                .withDelay(TimeUnit.MILLISECONDS, 10);
                    } else {
                        return HttpResponse.response()
                                .withStatusCode(200)
                                .withBody(String.valueOf(requestBody.length()))
                                .withDelay(TimeUnit.MILLISECONDS, 10);
                    }
                });
    }

    public void registerRestfulGetEndpoint() throws JsonProcessingException {
        String requestJson = ResourceHelper.readJsonFile("restfulGetRequest.json");
        String responseJson = ResourceHelper.readJsonFile("restfulGetResponse.json");
        HttpRequest httpRequest = HttpHelper.createHttpRequestFromJson(requestJson);
        HttpResponse httpResponse = HttpHelper.createHttpResponseFromJson(responseJson);
        mockServerClient.when(httpRequest).respond(httpResponse);
    }

    public void registerSoapEndpoint() {
        String requestXml = ResourceHelper.readXmlFile("soapRequest.xml");
        String responseXml = ResourceHelper.readXmlFile("soapResponse.xml");
        mockServerClient.when(request()
                                .withBody(requestXml))
                .respond(HttpResponse.response()
                                .withStatusCode(200)
                                .withBody(responseXml)
                                .withHeader("Content-Type", "text/xml"));
    }

    public void registerSoapLenEndpoint() {
        String requestXml = ResourceHelper.readXmlFile("soapRequest.xml");
        mockServerClient.when(request()
                        .withBody(requestXml))
                .respond(HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(String.valueOf(requestXml.length()))
                        .withHeader("Content-Type", "text/xml"));
    }
}