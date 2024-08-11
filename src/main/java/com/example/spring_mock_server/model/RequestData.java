package com.example.spring_mock_server.model;

import lombok.Data;

@Data
public class RequestData {
    private String method;
    private String path;
    private String body;
}
