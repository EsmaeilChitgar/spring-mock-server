package com.example.spring_mock_server.helper;

import com.example.spring_mock_server.model.RequestData;
import com.example.spring_mock_server.model.ResponseData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

public class HttpHelper {
    public static HttpRequest createHttpRequestFromJson(String requestJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        RequestData requestData = objectMapper.readValue(requestJson, RequestData.class);
        return HttpRequest.request()
                .withMethod(requestData.getMethod())
                .withPath(requestData.getPath());
    }

    public static HttpResponse createHttpResponseFromJson(String requestJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseData responseData = objectMapper.readValue(requestJson, ResponseData.class);
        return HttpResponse.response()
                .withBody(objectMapper.writeValueAsString(responseData.getBody()))
                .withStatusCode(responseData.getStatusCode());
    }
}
