package com.example.spring_mock_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidNumericException extends RuntimeException {
    static final long serialVersionUID = 55267766751552729L;
    public InvalidNumericException(String message) {
        super(message);
    }
}
