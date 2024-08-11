package com.example.spring_mock_server.model;

import lombok.Data;

@Data
public class ResponseData {
    private Integer statusCode;
    private Body body;

    @Data
    static class Body {
        private String message;
    }
}
